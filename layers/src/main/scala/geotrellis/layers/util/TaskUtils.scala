package geotrellis.layers.util

import cats.effect._
import cats.syntax.all._

import scala.concurrent.duration._
import scala.util.Random

object TaskUtils extends App {
  /**
    * Implement non-blocking Exponential Backoff on a Task.
    * @param  p  returns true for exceptions that trigger a backoff and retry
    */

  implicit class IOBackoff[A, F[_]: Effect: Timer: Sync](ioa: F[A]) {
    def retryEBO(p: (Throwable => Boolean)): F[A] = {
      def help(count: Int): F[A] = {
        val base: Duration = 52.milliseconds
        val timeout = base * Random.nextInt(math.pow(2, count).toInt) // .extInt is [), implying -1
        val actualDelay = FiniteDuration(timeout.toMillis, MILLISECONDS)

        ioa.handleErrorWith { error =>
          if(p(error)) implicitly[Timer[F]].sleep(actualDelay) *> help(count + 1)
          else implicitly[Sync[F]].raiseError(error)
        }
      }
      help(0)
    }
  }
}
