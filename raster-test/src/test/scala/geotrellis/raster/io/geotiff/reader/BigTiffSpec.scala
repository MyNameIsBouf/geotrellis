package geotrellis.raster.io.geotiff.reader

import geotrellis.util.{StreamByteReader, LocalBytesStreamer}
import geotrellis.raster.testkit._
import geotrellis.raster.io.geotiff._
import org.scalatest._

class BigTiffSpec extends FunSpec
  with Matchers
  with RasterMatchers
  with GeoTiffTestUtils {

  describe("Reading BigTiffs") {
    val path = geoTiffPath("bigtiffs/3bands-striped-band-bigtiff.tif")
    val chunkSize = 150
    val local = new LocalBytesStreamer(path, chunkSize)
    println(local.objectLength)
    val reader = StreamByteReader(local)
    //val path = geoTiffPath("bigtiffs/aspect_bit_uncompressed_striped_bigtiff.tif")

    it("should read the tiffTags of a bigTiff") {
      TiffTagsReader.read(reader)
      //TiffTagsReader.read(path)
    }
  }
}
