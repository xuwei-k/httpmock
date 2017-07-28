package httpmock

import play.api.mvc.Headers

final class Expectation(logs: AccessLogQueue) {
  def expect(
    methodNullable: HttpMethod = null,
    headers: Headers = Headers(),
    bodyOpt: Option[ArrayByte] = None,
    count: Int = 1
  ): EachLogExpectationBuilder = new EachLogExpectationBuilder(
    methodOpt = Option(methodNullable),
    headers = headers,
    bodyOpt = bodyOpt,
    count = count,
    logs
  )
}
