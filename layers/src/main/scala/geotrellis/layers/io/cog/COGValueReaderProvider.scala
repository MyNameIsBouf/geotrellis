package geotrellis.layers.io.cog

import geotrellis.layers.LayerId
import geotrellis.layers.io.AttributeStore

import java.net.URI


trait COGValueReaderProvider {
  def canProcess(uri: URI): Boolean

  def valueReader(uri: URI, store: AttributeStore): COGValueReader[LayerId]
}
