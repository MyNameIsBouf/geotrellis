package geotrellis.util

import geotrellis.raster._
import geotrellis.raster.testkit._

import java.nio.file.{ Paths, Files }

import org.scalatest._

    /*
class LocalBytesStreamerSpec extends FunSpec
  with Matchers
  with GeoTiffTestUtils {


  describe("Streaming bytes locally") {
    val path = geoTiffPath("bigtiffs/aspect_byte_uncompressed_tiled_bigtiff.tif")
    val geoTiffBytes = Files.readAllBytes(Paths.get(path))
    val chunkSize = 20000
    val local = new MockLocalArrayBytes(geotTiffBytes, chunkSize) 


    def testArrays[T](arr1: Array[T], arr2: Array[T]): Array[(T, T)] = {
      val zipped = arr1.zip(arr2)
      zipped.filter(x => x._1 != x._2)
    }

    it("should return the correct bytes") {
      val actual = s3Bytes.getArray(0.toLong)
      val expected = Array.ofDim[Byte](chunkSize)

      cfor(0)(_ < chunkSize, _ + 1) { i=>
        expected(i) = local.get
      }
      local.position(0)

      val result = testArrays(actual, expected)

      result.length should be (0)
    }

    it("should return the correct bytes throught the file") {
      cfor(0)(_ < s3Bytes.objectLength - chunkSize, _ + chunkSize){ i =>
        val actual = s3Bytes.getArray(i.toLong, chunkSize.toLong)
        val expected = Array.ofDim[Byte](chunkSize)
        
        cfor(0)(_ < chunkSize, _ + 1) { j =>
          expected(j) = local.get
        }

        val result = testArrays(actual, expected)

        result.length should be (0)
      }
      local.position(0)
    }

    it("should return the correct offsets for each chunk") {
      val actual = Array.range(0, 420000, chunkSize).map(_.toLong)
      val expected = Array.ofDim[Long](400000 / chunkSize)
      var counter = 0

      cfor(0)(_ < 400000, _ + chunkSize){ i =>
        expected(counter) = s3Bytes.getMappedArray(i.toLong, chunkSize).head._1
        counter += 1
      }

      val result = testArrays(actual, expected)

      result.length should be (0)
    }

    it("should not read past the end of the file") {
      val start = s3Bytes.objectLength - 100
      val actual = s3Bytes.getArray(start, start + 300)
      val arr = Array.ofDim[Byte](100)
      local.position(start.toInt)

      val expected = {
        cfor(0)(_ < 100, _ + 1){ i =>
          arr(i) = local.get
        }
        arr
      }
      local.position(0)

      val result = testArrays(expected, actual)
      
      result.length should be (0)
    }
  }
}
  */
