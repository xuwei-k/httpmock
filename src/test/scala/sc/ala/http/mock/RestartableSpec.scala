package sc.ala.http.mock

import java.util.concurrent.ExecutionException

class RestartableSpec extends TestHelper {
  describe("HttpMock") {
    val url : String = s"http://127.0.0.1:$testPort"
    def ok(): Unit   = assert(get(url).getStatusCode === 200)
    def ng(): Unit   = intercept[ExecutionException] { get(url) }

    it("can stop and start") {
      val started : HttpMock = autoStop(HttpMock.start(testPort))
      ok()

      val stopped: HttpMock = started.stop
      ng()

      val restarted: HttpMock = autoStop(stopped.start)
      ok()
    }
  }
}
