package sc.ala.http.mock

import play.api.mvc.Headers
import scala.concurrent.duration._


trait Expectation { this: AccessLogQueue =>
  private val queue = this
  lazy val expect = Expect

  case class Expect(
    method  : String = "",  // match everything by startsWith
    headers : Headers = Headers(),
    bodyOpt : Option[Array[Byte]] = None,
    count   : Int = 1
  ) {
    def body(v: String) : Expect = copy(bodyOpt = Some(v.getBytes()))
    def count(v: Int)   : Expect = copy(count = v)

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

    def header(key: String, value: String): Expect = copy(headers = headers.add((key, value)))

    protected def isMatchMethod(log: AccessLog): Boolean = log.request.method.startsWith(method)

    protected def isMatchHeader(log: AccessLog): Boolean = {
      val expected = headers.headers
      val accessed = log.request.headers.headers
      expected.forall { case (k1, v1) =>
        accessed.exists{ case (k2, v2) =>
          k1 == k2 && v1 == v2
        }
      }
    }

    protected def isMatchBody(log: AccessLog): Boolean = {
      // def debug(msg: String) = { println("#"*80); println(s"isMatchBody: $msg") }
      def debug(msg: String) = {}
        
      bodyOpt match {
        case None =>
          debug("true (left is None)")
          true
        case Some(_)if log.bodyOpt == None =>
          debug("false (right is None)")
          false
        case Some(b1) =>
          val b2 = log.bodyOpt.get
          java.util.Arrays.equals(b1, b2)
      }
    }

    protected def isMatch(log: AccessLog): Boolean = isMatchBody(log) && isMatchMethod(log) && isMatchHeader(log)

    protected def foundCount(): Int = {
      var found = 0
      val it = queue.iterator()
      while (it.hasNext) {
        val log = it.next()
        if (isMatch(log))
          found += 1
      }
      found
    }

    protected def now() = System.currentTimeMillis()
  }
}
