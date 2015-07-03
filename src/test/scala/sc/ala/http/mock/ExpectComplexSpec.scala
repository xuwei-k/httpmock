package sc.ala.http.mock

import java.lang.AssertionError
import scala.concurrent.duration._

class ExpectComplexSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("complex conditions") {
    it("header with count") {
      HttpMock.run(testPort) { server =>
        get(url)
        post(url, "body", headers = Map("A" -> "1"))
        post(url, "body", headers = Map("B" -> "1"))

        server.logs.expect(count = 3)(1.second)
        server.logs.expect(POST, count = 2)(1.second)
        server.logs.expect(POST).header("A", "1")(1.second)
        intercept[AssertionError] {
          server.logs.expect(POST, count = 2).header("A", "1")(0.5.second)
        }
      }
    }
  }
}
