package httpmock

import scala.concurrent.duration._

final class EachLogExecutionContext(builder: EachLogExpectationBuilder) {

  import builder._

  def run(timeout: FiniteDuration, interval: Int): Unit = {
    val startedAt = now()
    def inTime(): Boolean = now() - startedAt < timeout.toMillis

    while (true) {
      foundCount() match {
        case got if got > count => throw new AssertionError(s"expected $count $method, but got $got")
        case got if got == count => return // successfully found
        case got if inTime() => // try again
        case got => throw new AssertionError(s"timeout: expected $count $method, but got $got (in $timeout)")
      }
      Thread.sleep(interval)
    }
  }

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

  private[this] def isMatchBody(log: AccessLog): Boolean = {
    // def debug(msg: String) = { println("#"*80); println(s"isMatchBody: $msg") }
    def debug(msg: String) = {}

    bodyOpt match {
      case None =>
        debug("true (left is None)")
        true
      case Some(_) if log.bodyOpt == None =>
        debug("false (right is None)")
        false
      case Some(b1) =>
        val b2 = log.bodyOpt.get
        b1 == b2
    }
  }

  private[this] def isMatch(log: AccessLog): Boolean = isMatchBody(log) && isMatchMethod(log) && isMatchHeader(log)

  private[this] def foundCount(): Int = queue.filter(isMatch).size
}
