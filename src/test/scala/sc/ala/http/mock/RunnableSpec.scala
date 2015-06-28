package sc.ala.http.mock

import java.util.concurrent.ExecutionException

class RunnableSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"
  def ok(): Unit   = assert(get(url).getStatusCode === 200)
  def ng(): Unit   = intercept[ExecutionException] { get(url) }

  describe("Setting()") {
    it("run in loan pattern") {
      Setting(port = testPort).run { server =>
        ok()
      }
      ng()
    }
  }

  describe("HttpMock") {
    it("run(port) in loan pattern") {
      HttpMock.run(testPort) { server =>
        ok()
      }
      ng()
    }
  }
}
