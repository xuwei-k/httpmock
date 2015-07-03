package sc.ala.http.mock

import java.lang.AssertionError

class ExpectComplexSpec extends TestHelper {
  val url : String = s"http://127.0.0.1:$testPort"

  describe("complex conditions") {
    it("header with count") {
      HttpMock.run(testPort) { server =>
        get(url)
        post(url, "body", headers = Map("A" -> "1"))
        post(url, "body", headers = Map("B" -> "1"))

        server.logs.expect(count = 3)              (timeout)
        server.logs.expect(POST, count = 2)        (timeout)
        server.logs.expect(POST).header("A", "1")  (timeout)

        intercept[AssertionError] {
          server.logs.expect(POST, count = 2).header("A", "1")  (timeout)
        }
      }
    }

    it("header with body and count") {
      HttpMock.run(testPort) { server =>
        get(url)
        post(url, "foo", headers = Map("X-ID" -> "1"))
        post(url, "bar", headers = Map("X-ID" -> "2"))
        post(url, "bar", headers = Map("X-ID" -> "2"))  // emmulates duplicated post

        server.logs.expect(count = 4)       (timeout)
        server.logs.expect(POST, count = 3) (timeout)
        server.logs.expect(POST).count(3)   (timeout)

        server.logs.expect(POST).header("X-ID", "1")           (timeout)
        server.logs.expect(POST).header("X-ID", "2").count(2)  (timeout)

        server.logs.expect(POST).header("X-ID", "1").body("foo")          (timeout)
        server.logs.expect(POST).header("X-ID", "2").body("bar").count(2) (timeout)

        intercept[AssertionError] {
          server.logs.expect(POST).header("X-ID", "1").body("bar") (timeout)
        }
      }
    }
  }
}
