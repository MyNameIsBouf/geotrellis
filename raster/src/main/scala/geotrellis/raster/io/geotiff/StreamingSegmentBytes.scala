package geotrellis.raster.io.geotiff

import geotrellis.util._
import geotrellis.raster._
import geotrellis.raster.io.geotiff.tags._
import geotrellis.raster.io.geotiff.util._
import geotrellis.vector.Extent

import spire.syntax.cfor._
import monocle.syntax.apply._

class StreamingSegmentBytes(byteReader: StreamingByteReader,
	segmentLayout: GeoTiffSegmentLayout,
	extent: Extent,
	tiffTags: TiffTags) extends SegmentBytes {

	val gridBounds: GridBounds = {
		val rasterExtent: RasterExtent =
			RasterExtent(tiffTags.extent, tiffTags.cols, tiffTags.rows)
		rasterExtent.gridBoundsFor(extent)
	}
	
	private val colMin: Int = gridBounds.colMin
	private val rowMin: Int = gridBounds.rowMin
	private val colMax: Int = gridBounds.colMax
	private val rowMax: Int = gridBounds.rowMax

	val segments: Array[Int] = 0 until tiffTags.segmentCount toArray

	val intersectingSegments: Array[Int] =
		segments.filter(x => {
			val segmentTransform = segmentLayout.getSegmentTransform(x)

			val startCol: Int = segmentTransform.indexToCol(0)
			val startRow: Int = segmentTransform.indexToRow(0)
			val endCol: Int = startCol + segmentTransform.segmentCols
			val endRow: Int = startRow + segmentTransform.segmentRows
			
			val startResult = (startCol <= colMax && startRow <= rowMax)
			val endResult = (endCol > colMin && endRow > rowMin)

			(startResult && endResult)

		}).toArray

	val remainingSegments: Array[Int] =
		segments.filter(x => !intersectingSegments.contains(x))

	val (offsets, byteCounts) =
		if (tiffTags.hasStripStorage) {
			val stripOffsets = (tiffTags &|->
				TiffTags._basicTags ^|->
				BasicTags._stripOffsets get)
			
			val stripByteCounts = (tiffTags &|->
				TiffTags._basicTags ^|->
				BasicTags._stripByteCounts get)

			(stripOffsets.get, stripByteCounts.get)

		} else {
			val tileOffsets = (tiffTags &|->
				TiffTags._tileTags ^|->
				TileTags._tileOffsets get)

			val tileByteCounts = (tiffTags &|->
				TiffTags._tileTags ^|->
				TileTags._tileByteCounts get)

			(tileOffsets.get, tileByteCounts.get)
		}
	
	private val compressedBytes: Array[Array[Byte]] = {
		val result = Array.ofDim[Array[Byte]](offsets.size)
				
		cfor(0)(_ < intersectingSegments.size, _ + 1) { i =>
			val value = intersectingSegments(i)
			result(value) = byteReader.getSignedByteArray(byteCounts(value), offsets(value))
		}
		result
	}

	override val size = offsets.size

	def getSegment(i: Int): Array[Byte] =
		if (intersectingSegments.contains(i))
			compressedBytes(i)
		else
			byteReader.getSignedByteArray(byteCounts(i), offsets(i))
}
