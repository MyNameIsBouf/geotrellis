package geotrellis.raster.update

import geotrellis.raster.{Tile, ArrayTile}

import spire.syntax.cfor._


/**
  * Trait containing extension methods for doing update operations on
  * single-band [[Tile]]s.
  */
trait SinglebandTileUpdateMethods extends TileUpdateMethods[Tile] {
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
  def updateFromSource(colOffset: Int, rowOffset: Int, source: Tile): Tile = {
    val mutable = self.mutable.copy.mutable
    if (self.cellType.isFloatingPoint) {
      cfor(0)(_ < source.rows, _ + 1) { r =>
        cfor(0)(_ < source.cols, _ + 1) { c =>
          mutable.setDouble(c + colOffset, r + rowOffset, source.getDouble(c, r))
        }
      }
    } else {
      cfor(0)(_ < source.rows, _ + 1) { r =>
        cfor(0)(_ < source.cols, _ + 1) { c =>
          mutable.set(c + colOffset, r + rowOffset, source.get(c, r))
        }
      }
    }
    mutable
  }
}
