package geotrellis.layers.io

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

import cats.effect.{IO, Timer}
import cats.syntax.apply._


object Njoiner {
  def njoin[K, V](
    ranges: Iterator[(BigInt, BigInt)],
    threads: Int
   )(readFunc: BigInt => Vector[(K, V)]): Vector[(K, V)] =
    njoinEBO[K, V](ranges, threads)(readFunc)(_ => false)

  def njoinEBO[K, V](
    ranges: Iterator[(BigInt, BigInt)],
    threads: Int
  )(readFunc: BigInt => Vector[(K, V)])(backOffPredicate: Throwable => Boolean): Vector[(K, V)] = {
    import geotrellis.layers.util.TaskUtils._

    val pool = Executors.newFixedThreadPool(threads)
    // TODO: remove the implicit on ec and consider moving the implicit timer to method signature
    implicit val ec = ExecutionContext.fromExecutor(pool)
    implicit val timer: Timer[IO] = IO.timer(ec)
    implicit val cs = IO.contextShift(ec)

    val indices: Iterator[BigInt] = ranges.flatMap { case (start, end) =>
      (start to end).toIterator
    }

    val index: fs2.Stream[IO, BigInt] = fs2.Stream.fromIterator[IO, BigInt](indices)

    val readRecord: (BigInt => fs2.Stream[IO, Vector[(K, V)]]) = { index =>
      fs2.Stream eval IO.shift(ec) *> IO { readFunc(index) }.retryEBO { backOffPredicate }
    }

    try {
      index
        .map(readRecord)
        .parJoin(threads)
        .compile
        .toVector
        .unsafeRunSync
        .flatten
    } finally pool.shutdown()
  }
}
