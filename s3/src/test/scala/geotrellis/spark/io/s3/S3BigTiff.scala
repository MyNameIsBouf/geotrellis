package geotrellis.spark.io.s3

import geotrellis.util._
import geotrellis.spark.io.s3.util._
import geotrellis.raster.testkit._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.io.geotiff.reader._
import geotrellis.vector.Extent

import spire.syntax.cfor._
import org.scalatest._

class S3BigTiffSpec extends FunSpec with RasterMatchers {
  describe("Reading bigtiff from s3") {
    //val bucket = "bigtiffs-test"
    //val key = "aspect_byte_uncompressed_tiled_bigtiff.tif"
    //val key = "aspect_bit_uncompressed_striped_bigtiff.tif"
    //val key = "ls8_int32-big.tif"
    //val client = S3Client.default
    //val chunkSize = 15000000

    /*
    val bucket = "gt-rasters"
    val key = "nlcd/2011/whole/nlcd_2011_landcover_2011_edition_2014_10_10.tif"
    val client = S3Client.default
    val chunkSize = 6500000
    val s3Bytes = S3BytesStreamer(bucket, key, client, chunkSize)
    val reader = StreamByteReader(s3Bytes)
    val tiffTags = TiffTagsReader.read(reader)
    val actual = TiffTagsReader.read(reader)
    val counts = actual.basicTags.stripByteCounts.get
    println(counts.mkString(" "))
    println(counts.size)
    */
    //val actual = GeoTiffReader.readSingleband(reader, false, false)

    //val path = "raster-test/data/geotiff-test-files/ls8_int32.tif"
    /*
    val expected = TiffTagsReader.read(path)
    val counts2 = expected.basicTags.stripByteCounts.get
    println(counts2.mkString(" "))
    println(counts2.size)
    //println(result.mkString(" "))
    val expected = GeoTiffReader.readSingleband(path, false, false)

    it("sould be equal") {
      val a = actual.tile
      assertEqual(actual.tile, expected.tile)
    }
    */
  }
}
