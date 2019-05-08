package geotrellis.layers.io

import java.net.URI

import geotrellis.layers.LayerId

trait ValueReaderProvider {
  def canProcess(uri: URI): Boolean

  def valueReader(uri: URI, store: AttributeStore): ValueReader[LayerId]
}
