package geotrellis.spark.io.s3.util

import geotrellis.util._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.io.geotiff.tags._
import geotrellis.raster.io.geotiff.reader._
import geotrellis.spark.util._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import geotrellis.vector._
import geotrellis.raster._

import org.apache.spark._
import com.amazonaws.services.s3.model._

import org.scalatest._
import org.scalatest._

class S3ByteStreamingSpec extends FunSpec {

	def time[R](block: => R): R = {
		val start: Long = System.nanoTime()
		val result = block
		val end: Long = System.nanoTime()
		val diff: Long = end - start
		val seconds: Long = diff / 1000000000.toLong
		println(s"Total time: $seconds")
		result
	}

	describe("Reading files from S3") {
		it("really shouldn't take that long") {
			implicit val sc = SparkUtils.createSparkContext("Test", new SparkConf(true).setMaster("local[*]"))
			val bucket = "bigtiffs-test"
			val prefix = "ls8_int32-big.tif"

			val conf = sc.hadoopConfiguration

			S3InputFormat.setBucket(conf, bucket)
			S3InputFormat.setPrefix(conf, prefix)

			val dimRdd =
				sc.newAPIHadoopRDD[GetObjectRequest, TiffTags, TiffTagsS3InputFormat](conf, classOf[TiffTagsS3InputFormat], classOf[GetObjectRequest], classOf[TiffTags])
					.mapValues { tiffTags => (tiffTags.cols, tiffTags.rows) }

			val windows =
				dimRdd.flatMap { 
					case (objectRequest, (cols, rows)) =>
						RasterReader.listWindows(cols, rows, Some(256)).map((objectRequest, _))
				}

			windows.collect

			val options = S3GeoTiffRDD.Options(maxTileSize=Some(256))

			val (objectRequest, pixelWindow) = windows.first

			val rr = implicitly[RasterReader[S3GeoTiffRDD.Options, (ProjectedExtent, Tile)]]
			val reader = StreamingByteReader(S3RangeReader(objectRequest, options.getS3Client()))

			time { rr.readWindow(reader, pixelWindow, options) }
		}
	}
}
