package geotrellis.raster.update

import geotrellis.util.MethodExtensions
import geotrellis.raster.CellGrid


trait TileUpdateMethods[V <: CellGrid] extends MethodExtensions[V] {
  def updateFromSource(colOffset: Int, rowOffset: Int, source: V): V
}
