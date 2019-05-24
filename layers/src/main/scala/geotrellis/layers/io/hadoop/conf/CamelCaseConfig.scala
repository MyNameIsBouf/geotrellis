package geotrellis.layers.io.hadoop.conf

import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping}

trait CamelCaseConfig {
  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))
}
