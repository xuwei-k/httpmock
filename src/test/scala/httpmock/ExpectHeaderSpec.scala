package httpmock

import java.lang.AssertionError

class ExpectHeaderSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("header") {
    it("single header") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10"))
        server.logs.expect(POST)  (timeout)
        server.logs.expect(POST).header("X-CID", "10")  (timeout)
        intercept[AssertionError] {
          server.logs.expect(POST).header("X-CID", "10").header("A","xxx")  (timeout)
        }
      }
    }

    it("multiple headers") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10", "Content-Type" -> "text/plain"))
        server.logs.expect(POST)  (timeout)
        server.logs.expect(POST).header("X-CID", "10")  (timeout)
        server.logs.expect(POST).header("X-CID", "10").header("Content-Type", "text/plain")  (timeout)
        intercept[AssertionError] {
          server.logs.expect(POST).header("X-CID", "").header("Content-Type", "text/plain")  (timeout)
        }
      }
    }

    it("throws AssertionError when method mismatch") {
      HttpMock.run(testPort) { server =>
        post(url, "body", headers = Map("X-CID" -> "10"))
        intercept[AssertionError] {
          server.logs.expect(GET).header("X-CID", "10")  (timeout)
        }
      }
    }
  }
}
