package geotrellis.layers.io.cog

import java.net.URI

import geotrellis.layers.LayerId
import geotrellis.layers.io.AttributeStore


trait COGCollectionLayerReaderProvider {
  def canProcess(uri: URI): Boolean

  def collectionLayerReader(uri: URI, store: AttributeStore): COGCollectionLayerReader[LayerId]
}
