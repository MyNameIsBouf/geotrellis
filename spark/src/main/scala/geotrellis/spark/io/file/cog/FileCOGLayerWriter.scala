package geotrellis.spark.io.file.cog

import geotrellis.raster._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.io.geotiff._
import geotrellis.tiling._
import geotrellis.spark._
import geotrellis.spark.io.AttributeStore
import geotrellis.spark.io.cog._
import geotrellis.spark.io.cog.vrt.VRT
import geotrellis.spark.io.cog.vrt.VRT.IndexedSimpleSource
import geotrellis.spark.io.file._
import geotrellis.spark.io.index._
import geotrellis.util.{ByteReader, Filesystem}

import spray.json.JsonFormat
import java.io.File

import scala.reflect.{ClassTag, classTag}

class FileCOGLayerWriter(
  val attributeStore: AttributeStore,
  catalogPath: String
) extends COGLayerWriter {
  implicit def getByteReader(uri: String): ByteReader = byteReader(uri)
  def uriExists(uri: String): Boolean = { val f = new File(uri); f.exists() && f.isFile }

  def writeCOGLayer[K: SpatialComponent: Ordering: JsonFormat: ClassTag, V <: CellGrid: GeoTiffReader: ClassTag](
    layerName: String,
    cogLayer: COGLayer[K, V],
    keyIndexes: Map[ZoomRange, KeyIndex[K]],
    mergeFunc: Option[(GeoTiff[V], GeoTiff[V]) => GeoTiff[V]] = None
  ): Unit = {
    /** Collect VRT into accumulators, to write everything and to collect VRT at the same time */
    val layerId0 = LayerId(layerName, 0)
    val sc = cogLayer.layers.head._2.sparkContext
    val samplesAccumulator = sc.collectionAccumulator[IndexedSimpleSource](VRT.accumulatorName(layerName))

    val catalogPathFile = new File(catalogPath).getAbsolutePath
    Filesystem.ensureDirectory(new File(catalogPathFile, layerName).getAbsolutePath)

    val storageMetadata = COGLayerStorageMetadata(cogLayer.metadata, keyIndexes)
    attributeStore.write(layerId0, COGAttributeStore.Fields.metadata, storageMetadata)

    val header =
      FileLayerHeader(
        keyClass = classTag[K].toString(),
        valueClass = classTag[V].toString(),
        path = catalogPath
      )
    attributeStore.write(layerId0, COGAttributeStore.Fields.header, header)

    for(zoomRange <- cogLayer.layers.keys.toSeq.sorted(Ordering[ZoomRange].reverse)) {
      val keyIndex = keyIndexes(zoomRange)
      val maxWidth = Index.digits(keyIndex.toIndex(keyIndex.keyBounds.maxKey))
      val keyPath = KeyPathGenerator(catalogPathFile, s"${layerName}/${zoomRange.slug}", keyIndex, maxWidth)
      Filesystem.ensureDirectory(new File(catalogPathFile, s"${layerName}/${zoomRange.slug}").getAbsolutePath)

      val vrt = VRT(cogLayer.metadata.tileLayerMetadata(zoomRange.minZoom))

      // Write each cog layer for each zoom range, starting from highest zoom levels.
      cogLayer.layers(zoomRange).foreach { case (key, cog) =>
        val path = s"${keyPath(key)}.${Extension}"

        mergeFunc match {
          case None =>
            cog.write(path, true)
            // collect VRT metadata
            (0 until cog.bandCount)
              .map { b =>
                val idx = Index.encode(keyIndex.toIndex(key), maxWidth)
                (idx.toLong, vrt.simpleSource(s"$idx.$Extension", b + 1, cog.cols, cog.rows, cog.extent))
              }
              .foreach(samplesAccumulator.add)

          case Some(_) if !uriExists(path) =>
            cog.write(path, true)
            // collect VRT metadata
            (0 until cog.bandCount)
              .map { b =>
                val idx = Index.encode(keyIndex.toIndex(key), maxWidth)
                (idx.toLong, vrt.simpleSource(s"$idx.$Extension", b + 1, cog.cols, cog.rows, cog.extent))
              }
              .foreach(samplesAccumulator.add)

          case Some(merge) if uriExists(path) =>
            val old = GeoTiffReader[V].read(path, decompress = false, streaming = true)
            val merged = merge(cog, old)
            merged.write(path, true)
            // collect VRT metadata
            (0 until merged.bandCount)
              .map { b =>
                val idx = Index.encode(keyIndex.toIndex(key), maxWidth)
                (idx.toLong, vrt.simpleSource(s"$idx.$Extension", b + 1, merged.cols, merged.rows, merged.extent))
              }
              .foreach(samplesAccumulator.add)
        }
      }

      vrt
        .fromAccumulator(samplesAccumulator)
        .write(s"${catalogPathFile}/${layerName}/${zoomRange.slug}/vrt.xml")

      samplesAccumulator.reset
    }
  }
}

object FileCOGLayerWriter {
  def apply(attributeStore: FileAttributeStore): FileCOGLayerWriter =
    new FileCOGLayerWriter(attributeStore, attributeStore.catalogPath)

  def apply(catalogPath: String): FileCOGLayerWriter =
    apply(FileAttributeStore(catalogPath))
}
