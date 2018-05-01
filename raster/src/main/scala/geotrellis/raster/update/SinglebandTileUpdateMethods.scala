package geotrellis.raster.update

import geotrellis.raster.{Tile, ArrayTile}
import geotrellis.raster.prototype._

import spire.syntax.cfor._


trait SinglebandTileUpdateMethods extends TileUpdateMethods[Tile] {
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
