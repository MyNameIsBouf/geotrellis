package geotrellis.raster.update

import geotrellis.util.MethodExtensions
import geotrellis.raster.CellGrid


/**
  * Trait guaranteeing extension methods for doing update operations on [[Tile]]s.
  */
trait TileUpdateMethods[V <: CellGrid] extends MethodExtensions[V] {
  /**
    * Update this [[Tile]] by pasting the other one into it starting at the
    * given column and row offsets. The resulting [[Tile]] will be an updated
    * copy of the original [[Tile]].
    *
    * @param  colOffset  The column offset
    * @param  rowOffset  The row offset
    * @param  source     The source tile
    * @return            A new Tile, the result of the update
    */
  def updateFromSource(colOffset: Int, rowOffset: Int, source: V): V
}
