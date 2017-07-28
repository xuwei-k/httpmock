package httpmock

class ExpectBodySpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("body") {
    it("single log") {
      HttpMock.run(testPort) { server =>
        post(url, "body")
        server.logs.expect(POST)               (timeout)
        server.logs.expect(POST).body("body")  (timeout)

        intercept[AssertionError] {
          server.logs.expect(POST).body("xxx") (timeout)
        }
      }
    }

    it("multiple logs") {
      HttpMock.run(testPort) { server =>
        post(url, "body")
        post(url, "body2")
        post(url, "body")

        server.logs.expect(POST, count = 3)             (timeout)
        server.logs.expect(POST).body("body").count(2)  (timeout)
        server.logs.expect(POST).body("body2")          (timeout)

        intercept[AssertionError] {
          server.logs.expect(POST).body("body3")        (timeout)
        }
      }
    }
  }
}
