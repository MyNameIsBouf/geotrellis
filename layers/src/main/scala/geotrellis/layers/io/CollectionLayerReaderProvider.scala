package geotrellis.layers.io

import geotrellis.layers.LayerId

import java.net.URI

trait CollectionLayerReaderProvider {

  def canProcess(uri: URI): Boolean

  def collectionLayerReader(uri: URI, store: AttributeStore): CollectionLayerReader[LayerId]

}
