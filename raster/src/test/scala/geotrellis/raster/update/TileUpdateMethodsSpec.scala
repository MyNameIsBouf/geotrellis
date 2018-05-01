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

package geotrellis.raster.update

import geotrellis.raster._
import geotrellis.raster.testkit._

import org.scalatest._

class TileUpdateMethodsSpec
    extends FunSpec
    with Matchers
    with TileBuilders
    with RasterMatchers
    with TestFiles {

  describe("Updating a SinglebandTile from another Source") {
    it("should update a Tile from another Tile") {
      val original = createTile(
        Array[Int](
          1, 1, 1, 1, 1,
          1, 2, 2, 2, 1,
          1, 2, 2, 2, 1,
          1, 2, 2, 2, 1,
          1, 1, 1, 1, 1))

      val source = createTile(
        Array[Int](
          3, 3, 3,
          3, 3, 3,
          3, 3, 3))

      val expected = createTile(
        Array[Int](
          1, 1, 1, 1, 1,
          1, 3, 3, 3, 1,
          1, 3, 3, 3, 1,
          1, 3, 3, 3, 1,
          1, 1, 1, 1, 1))

      val actual = original.updateFromSource(1, 1, source)

      assertEqual(actual, expected)
    }
  }

  describe("Updating a MultibandTile from another Source") {
      val band1 = createTile(
        Array[Int](
          1, 1, 1,
          1, 1, 1,
          1, 1, 1))

      val band2 = createTile(
        Array[Int](
          2, 2, 2,
          2, 2, 2,
          2, 2, 2))

      val band3 = createTile(
        Array[Int](
          3, 3, 3,
          3, 3, 3,
          3, 3, 3))

      val original = MultibandTile(band1, band2, band3)

    it("should update a MultibandTile from another Tile") {
      val source = createTile(
        Array[Int](
          4,
          4,
          4), 1, 3)

      val expectedBand1 = createTile(
        Array[Int](
          1, 1, 4,
          1, 1, 4,
          1, 1, 4))

      val expectedBand2 = createTile(
        Array[Int](
          2, 2, 4,
          2, 2, 4,
          2, 2, 4))

      val expectedBand3 = createTile(
        Array[Int](
          3, 3, 4,
          3, 3, 4,
          3, 3, 4))

      val expected = MultibandTile(expectedBand1, expectedBand2, expectedBand3)
      val actual = original.updateFromSource(2, 0, source)

      assertEqual(actual, expected)
    }

    it("should update a MultibandTile from another MultibandTile") {
      val sourceBand1 = createTile(Array[Int](4, 4, 4), 3, 1)
      val sourceBand2 = createTile(Array[Int](5, 5, 5), 3, 1)
      val sourceBand3 = createTile(Array[Int](6, 6, 6), 3, 1)

      val source = MultibandTile(sourceBand1, sourceBand2, sourceBand3)

      val expectedBand1 = createTile(
        Array[Int](
          1, 1, 1,
          1, 1, 1,
          4, 4, 4))

      val expectedBand2 = createTile(
        Array[Int](
          2, 2, 2,
          2, 2, 2,
          5, 5, 5))

      val expectedBand3 = createTile(
        Array[Int](
          3, 3, 3,
          3, 3, 3,
          6, 6, 6))

      val expected = MultibandTile(expectedBand1, expectedBand2, expectedBand3)
      val actual = original.updateFromSource(0, 2, source)

      assertEqual(actual, expected)
    }
  }
}
