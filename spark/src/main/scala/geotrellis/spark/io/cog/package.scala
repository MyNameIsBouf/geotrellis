package geotrellis.spark.io

import geotrellis.tiling._
import geotrellis.vector.Extent

import org.apache.spark.util.AccumulatorV2
import java.util

package object cog extends Implicits {
  val GTKey     = "GT_KEY"
  val Extension = "tiff"

  object COGAttributeStore {
    object Fields {
      val metadata = "cog_metadata"
      val header   = "header"
    }
  }

  implicit class withExtentMethods(extent: Extent) {
    def bufferByLayout(layout: LayoutDefinition): Extent =
      Extent(
        extent.xmin + layout.cellwidth / 2,
        extent.ymin + layout.cellheight / 2,
        extent.xmax - layout.cellwidth / 2,
        extent.ymax - layout.cellheight / 2
      )
  }
}
