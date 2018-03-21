/*
 * Copyright 2017 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.spark.io.cog

import geotrellis.raster._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.merge._
import geotrellis.raster.prototype._
import geotrellis.raster.crop._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.io.geotiff.compression.{Compression, NoCompression}
import geotrellis.tiling._
import geotrellis.spark._
import geotrellis.spark.io.{AttributeNotFoundError, AttributeStore, LayerNotFoundError, LayerOutOfKeyBoundsError}
import geotrellis.spark.io.index._
import geotrellis.util.LazyLogging

import org.apache.spark.rdd.RDD
import spray.json._

import scala.reflect._

trait COGLayerWriter extends LazyLogging with Serializable {
  val attributeStore: AttributeStore

  def writeCOGLayer[
    K: SpatialComponent: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: GeoTiffReader: ClassTag
  ](
    layerName: String,
    cogLayer: COGLayer[K, V],
    keyIndexes: Map[ZoomRange, KeyIndex[K]],
    mergeFunc: Option[(GeoTiff[V], GeoTiff[V]) => GeoTiff[V]] = None
  ): Unit

  def write[
    K: SpatialComponent: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
     layerName: String,
     tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
     tileZoom: Int,
     keyIndexMethod: KeyIndexMethod[K]
   ): Unit = write[K, V](layerName, tiles, tileZoom, keyIndexMethod, NoCompression, None)

  def write[
    K: SpatialComponent: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
    layerName: String,
    tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
    tileZoom: Int,
    keyIndexMethod: KeyIndexMethod[K],
    compression: Compression,
    mergeFunc: Option[(GeoTiff[V], GeoTiff[V]) => GeoTiff[V]]
  ): Unit =
    tiles.metadata.bounds match {
      case keyBounds: KeyBounds[K] =>
        val cogLayer = COGLayer.fromLayerRDD(tiles, tileZoom, compression = compression)
        // println(cogLayer.metadata.toJson.prettyPrint)
        val keyIndexes: Map[ZoomRange, KeyIndex[K]] =
          cogLayer.metadata.zoomRangeInfos.
            map { case (zr, bounds) => zr -> keyIndexMethod.createIndex(bounds) }.
            toMap
        writeCOGLayer(layerName, cogLayer, keyIndexes, mergeFunc)
      case EmptyBounds =>
        throw new EmptyBoundsError("Cannot write layer with empty bounds.")
    }

  def write[
    K: SpatialComponent: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
     layerName: String,
     tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
     tileZoom: Int,
     keyIndex: KeyIndex[K]
   ): Unit = write[K, V](layerName, tiles, tileZoom, keyIndex, NoCompression, None)

  def write[
    K: SpatialComponent: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
     layerName: String,
     tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
     tileZoom: Int,
     keyIndex: KeyIndex[K],
     compression: Compression,
     mergeFunc: Option[(GeoTiff[V], GeoTiff[V]) => GeoTiff[V]]
   ): Unit =
    tiles.metadata.bounds match {
      case keyBounds: KeyBounds[K] =>
        val cogLayer = COGLayer.fromLayerRDD(tiles, tileZoom, compression = compression)
        // println(cogLayer.metadata.toJson.prettyPrint)
        val keyIndexes: Map[ZoomRange, KeyIndex[K]] =
          cogLayer.metadata.zoomRangeInfos.
            map { case (zr, _) => zr -> keyIndex }.
            toMap

        writeCOGLayer(layerName, cogLayer, keyIndexes, mergeFunc)
      case EmptyBounds =>
        throw new EmptyBoundsError("Cannot write layer with empty bounds.")
    }

  def overwrite[
    K: SpatialComponent: Boundable: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
    layerName: String,
    tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
    tileZoom: Int,
    compression: Compression = NoCompression
  ): Unit =
    if(tiles.metadata.bounds.nonEmpty) update[K, V](layerName, tiles, tileZoom, compression, None)
    else logger.info("Skipping layer update with empty bounds rdd.")

  def update[
    K: SpatialComponent: Boundable: Ordering: JsonFormat: ClassTag,
    V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V]: ? => TileCropMethods[V]: GeoTiffReader: GeoTiffBuilder
  ](
     layerName: String,
     tiles: RDD[(K, V)] with Metadata[TileLayerMetadata[K]],
     tileZoom: Int,
     compression: Compression = NoCompression,
     mergeFunc: Option[(GeoTiff[V], GeoTiff[V]) => GeoTiff[V]]
   ): Unit = {
    (tiles.metadata.bounds, mergeFunc) match {
      case (keyBounds: KeyBounds[K], _) =>
        val COGLayerStorageMetadata(metadata, keyIndexes) =
          try {
            attributeStore.read[COGLayerStorageMetadata[K]](LayerId(layerName, 0), "cog_metadata")
          } catch {
            // to follow GeoTrellis Layer Readers logic
            case e: AttributeNotFoundError => throw new LayerNotFoundError(LayerId(layerName, 0)).initCause(e)
          }

        val indexKeyBounds = keyIndexes(metadata.zoomRangeFor(tileZoom)).keyBounds

        // TODO: doublecheck this condition, it's more a workaround at the moment
        if(!indexKeyBounds.contains(keyBounds) && !metadata.keyBoundsForZoom(tileZoom).contains(keyBounds))
          throw new LayerOutOfKeyBoundsError(LayerId(layerName, tileZoom), indexKeyBounds)

        val cogLayer = COGLayer.fromLayerRDD(tiles, tileZoom, compression = compression)
        val ucogLayer = cogLayer.copy(metadata = cogLayer.metadata.combine(metadata))

        writeCOGLayer(layerName, ucogLayer, keyIndexes, mergeFunc)
      case (EmptyBounds, _) =>
        throw new EmptyBoundsError("Cannot write layer with empty bounds.")
    }
  }
}
