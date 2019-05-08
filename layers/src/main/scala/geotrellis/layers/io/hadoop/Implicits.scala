package geotrellis.layers.io.hadoop

import geotrellis.raster.CellGrid
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.raster.render.{Jpg, Png}
import geotrellis.util.MethodExtensions

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path


object Implicits extends Implicits

trait Implicits {
  implicit class withJpgHadoopWriteMethods(val self: Jpg) extends MethodExtensions[Jpg] {
    def write(path: Path, conf: Configuration): Unit =
      HdfsUtils.write(path, conf) { _.write(self.bytes) }
  }

  implicit class withPngHadoopWriteMethods(val self: Png) extends MethodExtensions[Png] {
    def write(path: Path, conf: Configuration): Unit =
      HdfsUtils.write(path, conf) { _.write(self.bytes) }
  }

  implicit class withGeoTiffHadoopWriteMethods[T <: CellGrid[Int]](val self: GeoTiff[T]) extends MethodExtensions[GeoTiff[T]] {
    def write(path: Path, conf: Configuration): Unit =
      HdfsUtils.write(path, conf) { new GeoTiffWriter(self, _).write() }
  }
}
