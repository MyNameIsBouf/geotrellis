package geotrellis

import geotrellis.raster._

package object layers extends Implicits {
  type RasterCollection[M] = Seq[Raster[Tile]] with Metadata[M]
  type MultibandRasterCollection[M] = Seq[Raster[MultibandTile]] with Metadata[M]

  type TileLayerCollection[K] = Seq[(K, Tile)] with Metadata[TileLayerMetadata[K]]
  object TileLayerCollection {
    def apply[K](seq: Seq[(K, Tile)], metadata: TileLayerMetadata[K]): TileLayerCollection[K] =
      new ContextCollection(seq, metadata)
  }

  type MultibandTileLayerCollection[K] = Seq[(K, MultibandTile)] with Metadata[TileLayerMetadata[K]]
  object MultibandTileLayerCollection {
    def apply[K](seq: Seq[(K, MultibandTile)], metadata: TileLayerMetadata[K]): MultibandTileLayerCollection[K] =
      new ContextCollection(seq, metadata)
  }
}
