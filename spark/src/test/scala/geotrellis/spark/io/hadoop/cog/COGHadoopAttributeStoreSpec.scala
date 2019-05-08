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

package geotrellis.spark.io.hadoop.cog

import geotrellis.spark._
import geotrellis.spark.io.{COGLayerType, LayerHeader}
import geotrellis.spark.io.cog._
import geotrellis.spark.io.hadoop._
import java.net.URI

import geotrellis.layers.io.hadoop.HadoopLayerHeader

class COGHadoopAttributeStoreSpec extends COGAttributeStoreSpec {
  lazy val attributeStore = HadoopAttributeStore(outputLocalPath)
  lazy val header = HadoopLayerHeader("geotrellis.spark.SpatialKey", "geotrellis.raster.Tile", new URI(outputLocalPath), COGLayerType)
}
