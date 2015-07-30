package sc.ala.http.mock

import scala.concurrent.duration._

private[mock] final class AllLogExecutionContext(builder: AllLogExpectationBuilder) {

  import builder._

  def run(timeout: FiniteDuration, interval: Int): Unit = {
    val startedAt = now()
    def inTime(): Boolean = now() - startedAt < timeout.toMillis

    while (true) {
      isMatchAll match {
        case true => return // successfully matched
        case false if inTime() => // try next
        case false => throw new AssertionError(s"timeout: expected $expected, but got $actual (in $timeout)")
      }
      Thread.sleep(interval)
    }
  }

  private[this] def expected = bodies

  private[this] def actual = queue.filter(isMatchEach).flatMap(_.bodyOpt.toSeq) // Seq("body1", "body2")

  private[this] def isMatchAll: Boolean = expected.diff(actual.toSet).isEmpty

  private[this] def isMatchMethod(log: AccessLog): Boolean = {
    methodOpt match {
      case None => true
      case Some(m) => log.request.method == m.value
    }
  }

  private[this] def isMatchHeader(log: AccessLog): Boolean = {
    val expected = headers.headers
    val accessed = log.request.headers.headers
    expected.forall { case (k1, v1) =>
      accessed.exists { case (k2, v2) =>
        k1 == k2 && v1 == v2
      }
    }
  }

  private[this] def isMatchEach(log: AccessLog): Boolean = isMatchMethod(log) && isMatchHeader(log)
}

