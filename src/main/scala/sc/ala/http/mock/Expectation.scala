package sc.ala.http.mock

import scala.concurrent.duration._

trait Expectation { this: AccessLogQueue =>
  private val queue = this
  lazy val expect = Expect

  case class Expect(
    method  : String = "",  // match everything by startsWith
    count   : Int = 0
  ) {
    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit = {
      val startedAt = now()
      def inTime(): Boolean = now() - startedAt < timeout.toMillis

      while(true) {
        foundCount() match {
          case got if got > count  => throw new AssertionError(s"expected $count ${method}, but got $got")
          case got if got == count => assert(true); return  // successfully found
          case got if inTime()     => // try again
          case got => throw new AssertionError(s"timeout: expected $count ${method}, but got $got (in $timeout)")
        }
        Thread.sleep(interval)
      }
    }

    protected def foundCount(): Int = {
      var found = 0
      val it = queue.iterator()
      while (it.hasNext) {
        val log = it.next()
        if (log.request.method.startsWith(method))
          found += 1
      }
      found
    }

    protected def now() = System.currentTimeMillis()
  }
}
