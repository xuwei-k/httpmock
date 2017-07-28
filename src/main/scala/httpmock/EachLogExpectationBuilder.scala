package httpmock

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

import akka.util.ByteString
import play.api.mvc.Headers

import scala.concurrent.duration._

final case class EachLogExpectationBuilder(
  methodOpt: Option[HttpMethod], // accept all method if None
  headers: Headers = Headers(),
  bodyOpt: Option[ArrayByte] = None, // body for sequential matcher
  count: Int = 1,
  queue: AccessLogQueue
) {
  def method: String = methodOpt.fold("")(_.value)

  def body(v: String, charset: Charset = UTF_8): EachLogExpectationBuilder =
    copy(bodyOpt = Some(ArrayByte(ByteString(v.getBytes(charset)))))

  def count(v: Int): EachLogExpectationBuilder = copy(count = v)

  def header(key: String, value: String): EachLogExpectationBuilder = copy(headers = headers.add((key, value)))

  /** converting */
  def bodies(bodies: Set[String], charset: Charset = UTF_8): AllLogExpectationBuilder = {
    // raise an error if both bodyOpt and bodies are given
    if (bodyOpt.isDefined) {
      throw new IllegalArgumentException("body and bodies are exclusive")
    }

    // raise an error if count > 1 set
    if (count > 1) {
      throw new IllegalArgumentException("bodies and count > 1 are exclusive")
    }

    AllLogExpectationBuilder(methodOpt, headers, bodies.map(x => ArrayByte(ByteString(x.getBytes(charset)))), queue)
  }

  /** await result */
  def apply(timeout: FiniteDuration = 1.second, interval: Int = 100): Unit = {
    new EachLogExecutionContext(this).run(timeout, interval)
  }
}
