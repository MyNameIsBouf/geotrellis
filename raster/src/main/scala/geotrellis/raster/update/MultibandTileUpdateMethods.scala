package geotrellis.raster.update

import geotrellis.raster.{Tile, MultibandTile}

/**
  * A trait containing extension methods related to MultibandTile
  * merging.
  */
trait MultibandTileUpdateMethods extends TileUpdateMethods[MultibandTile] {
  /**
    * Update this [[MultibandTile]] by pasting each band of the other [[MultibandTile]] into the
    * original's corresponding band starting at the given column and row offsets. The resulting
    * [[MultibandTile]] will be an updated copy of the original [[MultibandTile]].
    *
    * @note This method requires that both [[MultibandTile]]s have same number of bands.
    *
    * @param  colOffset  The column offset
    * @param  rowOffset  The row offset
    * @param  source     The source MultibandTile
    * @return            A new Tile, the result of the update
    */
  def updateFromSource(colOffset: Int, rowOffset: Int, source: MultibandTile): MultibandTile =
    MultibandTile(
      for (index <- 0 until self.bandCount) yield self.band(index).updateFromSource(colOffset, rowOffset, source.band(index))
    )

  /**
    * Update this [[MultibandTile]] by pasting a single [[Tile]] into
    * each band starting at the given column and row offsets. The resulting
    * [[MultibandTile]] will be an updated copy of the original [[MultibandTile]].
    *
    * @param  colOffset  The column offset
    * @param  rowOffset  The row offset
    * @param  source     The source MultibandTile
    * @return            A new Tile, the result of the update
    */
  def updateFromSource(colOffset: Int, rowOffset: Int, source: Tile): MultibandTile =
    MultibandTile(
      for (index <- 0 until self.bandCount) yield self.band(index).updateFromSource(colOffset, rowOffset, source)
    )
}
