package geotrellis.layers.io

import geotrellis.tiling.{Bounds, Boundable}
import geotrellis.layers._
import geotrellis.layers.io.avro.AvroRecordCodec
import geotrellis.layers.io.index._
import geotrellis.layers.io.json._
import geotrellis.util._

import scala.reflect.ClassTag
import spray.json._


trait LayerManager[ID] {
  def delete(id: ID): Unit

  def copy[
    K: AvroRecordCodec: Boundable: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat: Component[?, Bounds[K]]
  ](from: ID, to: ID): Unit

  def move[
    K: AvroRecordCodec: Boundable: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat: Component[?, Bounds[K]]
  ](from: ID, to: ID): Unit

  def reindex[
    K: AvroRecordCodec: Boundable: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat: Component[?, Bounds[K]]
  ](id: ID, keyIndexMethod: KeyIndexMethod[K]): Unit

  def reindex[
    K: AvroRecordCodec: Boundable: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat: Component[?, Bounds[K]]
  ](id: ID, keyIndex: KeyIndex[K]): Unit

}
