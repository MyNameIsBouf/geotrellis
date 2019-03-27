/*
 * Copyright 2018 Azavea
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

package geotrellis.raster.mask

import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.util.MethodExtensions
import geotrellis.vector._


abstract class SinglebandRasterTileFeatureMaskMethods[D] extends MethodExtensions[TileFeature[Raster[Tile], D]] {
  def mask(geom: Geometry): TileFeature[Raster[Tile], D] =
    TileFeature(self.tile.mask(geom), self.data)

  def mask(geom: Geometry, options: Rasterizer.Options): TileFeature[Raster[Tile], D] =
    TileFeature(self.tile.mask(geom, options), self.data)

  def mask(geoms: Traversable[Geometry]): TileFeature[Raster[Tile], D] =
    TileFeature(self.tile.mask(geoms), self.data)

  def mask(geoms: Traversable[Geometry], options: Rasterizer.Options): TileFeature[Raster[Tile], D] =
    TileFeature(self.tile.mask(geoms, options), self.data)
}

abstract class MultibandRasterTileFeatureMaskMethods[D] extends MethodExtensions[TileFeature[Raster[MultibandTile], D]] {
  def mask(geom: Geometry): TileFeature[Raster[MultibandTile], D] =
    TileFeature(self.tile.mask(geom), self.data)

  def mask(geom: Geometry, options: Rasterizer.Options): TileFeature[Raster[MultibandTile], D] =
    TileFeature(self.tile.mask(geom, options), self.data)

  def mask(geoms: Traversable[Geometry]): TileFeature[Raster[MultibandTile], D] =
    TileFeature(self.tile.mask(geoms), self.data)

  def mask(geoms: Traversable[Geometry], options: Rasterizer.Options): TileFeature[Raster[MultibandTile], D] =
    TileFeature(self.tile.mask(geoms, options), self.data)
}
