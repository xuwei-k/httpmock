package sc.ala.http.mock

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import play.api.mvc.Headers
import scala.concurrent.duration._

trait ExpectationBuilder {
  def methodNullable: HttpMethod
  def headers : Headers

  def method: String = methodOpt.fold("")(_.value)
  def methodOpt: Option[HttpMethod] = Option(methodNullable)
}

trait Expectation {
  private[mock] val queue = new AccessLogQueue()
  lazy val expect = EachLogExpectationBuilder

  case class EachLogExpectationBuilder(
    methodNullable: HttpMethod = null, // accept all method if null
    headers : Headers = Headers(),
    bodyOpt : Option[ArrayByte] = None,  // body for sequential matcher
    count   : Int = 1
  ) extends ExpectationBuilder {
    /** setters */
    def body(v: String, charset: Charset = UTF_8) : EachLogExpectationBuilder = copy(bodyOpt = Some(ArrayByte(v.getBytes(charset))))

    def count(v: Int) : EachLogExpectationBuilder = copy(count = v)

    def header(key: String, value: String): EachLogExpectationBuilder = copy(headers = headers.add((key, value)))

    /** converting */
    def bodies(bodies: Iterable[String], charset: Charset = UTF_8) : AllLogExpectationBuilder = {
      // raise an error if both bodyOpt and bodies are given
      if (bodyOpt.isDefined) {
        throw new IllegalArgumentException("body and bodies are exclusive")
      }

      // raise an error if count > 1 set
      if (count > 1) {
        throw new IllegalArgumentException("bodies and count > 1 are exclusive")
      }

      AllLogExpectationBuilder(methodNullable, headers, bodies.map(x => ArrayByte(x.getBytes(charset))))
    }

    /** await result */
    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit = {
      EachLogExecutionContext(this)(timeout, interval)
    }
  }

  case class AllLogExpectationBuilder(
    methodNullable: HttpMethod = null, // accept all method if null
    headers : Headers = Headers(),
    bodies  : Iterable[ArrayByte] = Iterable()
  ) extends ExpectationBuilder {
    /** converting */
    def header(key: String, value: String): AllLogExpectationBuilder = copy(headers = headers.add((key, value)))

    /** await result */
    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit = {
      AllLogExecutionContext(this)(timeout, interval)
    }
  }

  trait ExpectationContext {
    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit
    protected def now() = System.currentTimeMillis()
  }

  case class EachLogExecutionContext(builder: EachLogExpectationBuilder) extends ExpectationContext {
    import builder._

    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit = {
      val startedAt = now()
      def inTime(): Boolean = now() - startedAt < timeout.toMillis

      while(true) {
        foundCount() match {
          case got if got > count  => throw new AssertionError(s"expected $count $method, but got $got")
          case got if got == count => return  // successfully found
          case got if inTime()     => // try again
          case got => throw new AssertionError(s"timeout: expected $count $method, but got $got (in $timeout)")
        }
        Thread.sleep(interval)
      }
    }

    protected def isMatchMethod(log: AccessLog): Boolean = {
      methodOpt match {
        case None => true
        case Some(m) => log.request.method == m.value
      }
    }

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
        case Some(_) if log.bodyOpt == None =>
          debug("false (right is None)")
          false
        case Some(b1) =>
          val b2 = log.bodyOpt.get
          b1 == b2
      }
    }

    protected def isMatch(log: AccessLog): Boolean = isMatchBody(log) && isMatchMethod(log) && isMatchHeader(log)

    protected def foundCount(): Int = queue.filter(isMatch).size
  }

  implicit class AllLogExecutionContext(val builder: AllLogExpectationBuilder) extends ExpectationContext {
    import builder._

    def apply(timeout : FiniteDuration = 1.second, interval : Int = 100): Unit = {
      val startedAt = now()
      def inTime(): Boolean = now() - startedAt < timeout.toMillis

      while(true) {
        isMatchAll match {
          case true => return  // successfully matched
          case false if inTime() =>  // try next
          case false => throw new AssertionError(s"timeout: expected $expected, but got $actual (in $timeout)")
        }
        Thread.sleep(interval)
      }
    }

    private def expected = bodies
    private def actual   = queue.filter(isMatchEach).flatMap(_.bodyOpt.toSeq)  // Seq("body1", "body2")

    private def isMatchAll: Boolean = {
      expected match {
        case set: Set[ArrayByte] => set.diff(actual.toSet).isEmpty
        case unknown => throw new AssertionError(s"${unknown.getClass.getName} is not supported for bodies")
      }
    }

    protected def isMatchMethod(log: AccessLog): Boolean = {
      methodOpt match {
        case None => true
        case Some(m) => log.request.method == m.value
      }
    }

    protected def isMatchHeader(log: AccessLog): Boolean = {
      val expected = headers.headers
      val accessed = log.request.headers.headers
      expected.forall { case (k1, v1) =>
        accessed.exists{ case (k2, v2) =>
          k1 == k2 && v1 == v2
        }
      }
    }

    protected def isMatchEach(log: AccessLog): Boolean = isMatchMethod(log) && isMatchHeader(log)
  }
}

