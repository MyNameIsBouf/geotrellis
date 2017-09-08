package geotrellis.raster.summary.polygonal

import geotrellis.raster._


object Implicits extends Implicits

trait Implicits {
  implicit class withSinglebandTilePolygonalSummaryMethods(
    val self: Tile
  ) extends SinglebandTilePolygonalSummaryMethods

  implicit class withMultibandTilePolygonalSummaryMethods(
    val self: MultibandTile
  ) extends MultibandTilePolygonalSummaryMethods

  implicit def toMultibandHandler[T](
    _handler: TilePolygonalSummaryHandler[T]
  ): MultibandTilePolygonalSummaryHandler[T] =
    new MultibandTilePolygonalSummaryHandler[T] {
      def handler: TilePolygonalSummaryHandler[T] = _handler
    }

  implicit def toTileHandler[T](
    _handler: MultibandTilePolygonalSummaryHandler[T]
  ): TilePolygonalSummaryHandler[T] =
    _handler.handler
}
