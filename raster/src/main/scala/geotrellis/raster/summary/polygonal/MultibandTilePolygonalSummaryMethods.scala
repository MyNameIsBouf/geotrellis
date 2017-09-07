/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.summary.polygonal

import geotrellis.raster._
import geotrellis.raster.histogram.Histogram
import geotrellis.util.MethodExtensions
import geotrellis.vector._


object MultibandTilePolygonalSummaryFunctions {
  def handleFullTile[T](multibandTile: MultibandTile, handler: TilePolygonalSummaryHandler[T]): Seq[T] =
    multibandTile.bands.map { handler.handleFullTile(_) }

  def handlePartialTile[T](raster: Raster[MultibandTile], polygon: Polygon, handler: TilePolygonalSummaryHandler[T]): Seq[T] = {
    val Raster(multibandTile, extent) = raster
    multibandTile.bands.map { tile => handler.handlePartialTile(Raster(tile, extent), polygon) }
  }
}


/**
  * Trait containing extension methods for doing polygonal summaries
  * on tiles.
  */
trait MultibandTilePolygonalSummaryMethods extends PolygonalSummaryMethods[MultibandTile] {
  import MultibandTilePolygonalSummaryFunctions._

  /**
    * Given a Polygon, an Extent, and a summary handler, generate the
    * summary of a polygonal area with respect to the present tile.
    */
  def polygonalSummary[T](extent: Extent, polygon: Polygon, handler: TilePolygonalSummaryHandler[T]): T = {
    val results: Seq[T] = {
      if(polygon.contains(extent)) {
        handleFullTile(self, handler)
      } else {
        polygon.intersection(extent) match {
          case PolygonResult(intersection) =>
            handlePartialTile(Raster(self, extent), intersection, handler)
          case MultiPolygonResult(mp) =>
            mp.polygons.map { intersection =>
              handlePartialTile(Raster(self, extent), intersection, handler)
            }.reduce { _ ++ _ }
          case _ => Seq()
        }
      }
    }
    handler.combineResults(results)
  }

  /**
    * Given a MultiPolygon, an Extent, and a summary handler, generate
    * the summary of a polygonal area with respect to the present
    * tile.
    */
  def polygonalSummary[T](extent: Extent, multiPolygon: MultiPolygon, handler: TilePolygonalSummaryHandler[T]): T = {
    val results = {
      if(multiPolygon.contains(extent)) {
        handleFullTile(self, handler)
      } else {
        multiPolygon.intersection(extent) match {
          case PolygonResult(intersection) =>
            handlePartialTile(Raster(self, extent), intersection, handler)
          case MultiPolygonResult(mp) =>
            mp.polygons.map { intersection =>
              handlePartialTile(Raster(self, extent), intersection, handler)
            }.reduce { _ ++ _ }
          case _ => Seq()
        }
      }
    }

    handler.combineResults(results)
  }
}
