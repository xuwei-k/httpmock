package httpmock

import play.api.mvc.Headers
import scala.concurrent.duration._

case class AllLogExpectationBuilder(
  methodOpt: Option[HttpMethod], // accept all method if None
  headers : Headers = Headers(),
  bodies  : Set[ArrayByte],
  val queue: AccessLogQueue
) {
  def method: String = methodOpt.fold("")(_.value)
  /** converting */
  def header(key: String, value: String): AllLogExpectationBuilder = copy(headers = headers.add((key, value)))

  /** await result */
  def apply(timeout: FiniteDuration = 1.second, interval: Int = 100): Unit = {
    new AllLogExecutionContext(this).run(timeout, interval)
  }
}
