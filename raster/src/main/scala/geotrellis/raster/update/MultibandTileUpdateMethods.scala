package geotrellis.raster.update

import geotrellis.raster.{Tile, MultibandTile}

trait MultibandTileUpdateMethods extends TileUpdateMethods[MultibandTile] {
  def updateFromSource(colOffset: Int, rowOffset: Int, source: MultibandTile): MultibandTile =
    MultibandTile(
      for (index <- 0 until self.bandCount) yield self.band(index).updateFromSource(colOffset, rowOffset, source.band(index))
    )

  def updateFromSource(colOffset: Int, rowOffset: Int, source: Tile): MultibandTile =
    MultibandTile(
      for (index <- 0 until self.bandCount) yield self.band(index).updateFromSource(colOffset, rowOffset, source)
    )
}
