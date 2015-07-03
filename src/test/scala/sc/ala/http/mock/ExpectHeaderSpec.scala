package sc.ala.http.mock

import java.lang.AssertionError
import scala.concurrent.duration._

class ExpectHeaderSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("header") {
    it("single header") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10"))
        server.logs.expect(POST)(1.second)
        server.logs.expect(POST).header("X-CID", "10")(1.second)
        intercept[AssertionError] {
          server.logs.expect(POST).header("X-CID", "10").header("A","xxx")(0.5.second)
        }
      }
    }

    it("multiple headers") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10", "Content-Type" -> "text/plain"))
        server.logs.expect(POST)(1.second)
        server.logs.expect(POST).header("X-CID", "10")(1.second)
        server.logs.expect(POST).header("X-CID", "10").header("Content-Type", "text/plain")(1.second)
        intercept[AssertionError] {
          server.logs.expect(POST).header("X-CID", "").header("Content-Type", "text/plain")(0.5.second)
        }
      }
    }

    it("throws AssertionError when method mismatch") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10"))
        intercept[AssertionError] {
          server.logs.expect(GET).header("X-CID", "10")(0.5.second)
        }
      }
    }
  }
}
