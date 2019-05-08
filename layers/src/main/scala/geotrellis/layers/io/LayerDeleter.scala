package geotrellis.layers.io

trait LayerDeleter[ID] {
  def delete(id: ID): Unit
}
