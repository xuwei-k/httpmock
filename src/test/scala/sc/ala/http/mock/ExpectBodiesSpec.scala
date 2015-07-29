package sc.ala.http.mock

class ExpectBodiesSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("bodies") {
    it("set") {
      HttpMock.run(testPort) { server =>
        post(url, "body")
        post(url, "body2")
        post(url, "body", headers = Map("X-CID" -> "10"))
        post(url, "body3")

        server.logs.expect(POST).bodies(Set("body"))           (timeout)
        server.logs.expect(POST).bodies(Set("body2"))          (timeout)
        server.logs.expect(POST).bodies(Set("body", "body2"))  (timeout)
        server.logs.expect(POST).bodies(Set("body3", "body"))  (timeout)
        server.logs.expect(POST).header("X-CID", "10").bodies(Set("body"))  (timeout)
        server.logs.expect(POST).bodies(Set("body")).header("X-CID", "10")  (timeout)

        intercept[AssertionError] {
          server.logs.expect(POST).bodies(Set("bodyX"))        (timeout)
        }
      }
    }

    it("throws IllegalArgumentException when non supported iterators are given") {
      HttpMock.run(testPort) { server =>
        intercept[IllegalArgumentException] {
          server.logs.expect(POST).body("foo").bodies(Set("body"))  (timeout)
        }
      }
    }

    it("throws IllegalArgumentException when both body and bodies are given") {
      HttpMock.run(testPort) { server =>
        intercept[IllegalArgumentException] {
          server.logs.expect(POST).body("foo").bodies(Set("body"))  (timeout)
        }
      }
    }

    it("throws IllegalArgumentException when both body and count > 1 given") {
      HttpMock.run(testPort) { server =>
        intercept[IllegalArgumentException] {
          server.logs.expect(POST, count = 2).bodies(Set("body"))  (timeout)
        }
      }
    }
  }
}
