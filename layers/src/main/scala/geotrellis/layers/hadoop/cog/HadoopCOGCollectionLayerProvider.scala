package geotrellis.layers.hadoop.cog

import geotrellis.layers.{LayerId, AttributeStore, AttributeStoreProvider}
import geotrellis.layers.cog.{COGCollectionLayerReaderProvider, COGValueReader, COGValueReaderProvider}
import geotrellis.layers.hadoop.{HadoopAttributeStore, SCHEMES}
import geotrellis.layers.hadoop.util.HdfsUtils
import geotrellis.util.UriUtils

import org.apache.hadoop.fs.Path
import org.apache.hadoop.conf.Configuration

import java.net.URI


/**
 * Provides [[HadoopAttributeStore]] instance for URI with `hdfs`, `hdfs+file`, `s3n`, `s3a`, `wasb` and `wasbs` schemes.
 * The uri represents Hadoop [[Path]] of catalog root.
 * `wasb` and `wasbs` provide support for the Hadoop Azure connector. Additional
 * configuration is required for this.
 * This Provider intentinally does not handle the `s3` scheme because the Hadoop implemintation is poor.
 * That support is provided by [[HadoopAttributeStore]]
 */
class HadoopCOGCollectionLayerProvider extends AttributeStoreProvider with COGValueReaderProvider with COGCollectionLayerReaderProvider {
  def canProcess(uri: URI): Boolean = uri.getScheme match {
    case str: String => SCHEMES contains str.toLowerCase
    case null => false
  }

  def attributeStore(uri: URI): AttributeStore = {
    val path = new Path(HdfsUtils.trim(uri))
    val conf = new Configuration()
    HadoopAttributeStore(path, conf)
  }

  def valueReader(uri: URI, store: AttributeStore): COGValueReader[LayerId] = {
    val _uri = HdfsUtils.trim(uri)
    val path = new Path(_uri)
    val params = UriUtils.getParams(_uri)
    val conf = new Configuration()
    new HadoopCOGValueReader(store, conf)
  }

  def collectionLayerReader(uri: URI, store: AttributeStore) = {
    val _uri = HdfsUtils.trim(uri)
    val path = new Path(_uri)
    val conf = new Configuration()
    HadoopCOGCollectionLayerReader(path, conf)
  }
}
