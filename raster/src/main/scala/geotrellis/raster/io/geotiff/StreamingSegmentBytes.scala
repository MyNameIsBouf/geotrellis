package geotrellis.raster.io.geotiff

import geotrellis.util._
import geotrellis.raster._
import geotrellis.raster.io.geotiff.tags._
import geotrellis.vector.Extent

import spire.syntax.cfor._
import monocle.syntax.apply._

import scala.math.Ordering.Implicits._

class StreamingSegmentBytes(byteReader: StreamingByteReader,
	segmentLayout: GeoTiffSegmentLayout,
	extent: Extent,
	tiffTags: TiffTags) {

	val gridBounds: GridBounds = {
		val rasterExtent: RasterExtent =
			RasterExtent(tiffTags.extent, tiffTags.cols, tiffTags.rows)
		rasterExtent.gridBoundsFor(extent)
	}
	
	private val colMin: Int = gridBounds.colMin
	private val rowMin: Int = gridBounds.rowMin
	private val colMax: Int = gridBounds.colMax
	private val rowMax: Int = gridBounds.rowMax

	val segments: Array[Int] = {
		(0 until tiffTags.segmentCount).filter(x => {
			val segmentTransform = segmentLayout.getSegmentTransform(x)
			val startCol: Int = segmentTransform.indexToCol(0)
			val startRow: Int = segmentTransform.indexToRow(0)
			val endCol: Int = startCol + segmentTransform.segmentCols
			val endRow: Int = startRow + segmentTransform.segmentRows
			
			val startResult = (startCol <= colMax && startRow <= rowMax)
			val endResult = (endCol > colMin && endRow > rowMin)

			(startResult && endResult)

		}).toArray
	}
}
