/*
 * Copyright 2017 Azavea
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

package geotrellis.raster.crop

import geotrellis.raster._
import geotrellis.vector._
import geotrellis.util.MethodExtensions

abstract class TileFeatureCropMethods[T <: CellGrid[Int]: (? => TileCropMethods[T]), D] extends CropMethods[TileFeature[T, D]] {
  import Crop.Options

  def crop(srcExtent: Extent, extent: Extent, options: Options): TileFeature[T, D] =
    TileFeature(self.tile.crop(srcExtent, extent, options), self.data)

  def crop(gb: GridBounds[Int], options: Options): TileFeature[T, D] =
    TileFeature(self.tile.crop(gb, options), self.data)
}

trait SinglebandTileFeatureCropMethods[D] extends TileFeatureCropMethods[Tile, D]
trait MultibandTileFeatureCropMethods[D] extends TileFeatureCropMethods[MultibandTile, D]
