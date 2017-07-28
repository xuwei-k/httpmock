package httpmock

class HttpMockSpec extends TestHelper {
  describe("start") {
    def start[A](action: HttpMock => A): Unit = {
      val server = HttpMock.start
      try { action(server) } finally { server.stop }
    }

    def anyPaths: Seq[String] = Seq("", "foo", "foo/bar")

    it("can create http service on some port") {
      start { server =>
        assert(server.port > 0)
      }
    }

    describe("can respond 200 for GET request with") {
      anyPaths.foreach { path =>
        it(s"path('/$path')") {
          start { server =>
            val url = server.url + path
            assert(get(url).getStatusCode === 200)
          }
        }
      }
    }

    describe("can respond 200 for POST request with") {
      anyPaths.foreach { path =>
        it(s"path('/$path')") {
          start { server =>
            val url = server.url + path
            assert(post(url, "data").getStatusCode === 200)
          }
        }
      }
    }
  }

  describe("start(port)") {
    it("can create http service on specified port") {
      val server = HttpMock.start(testPort)
      try {
        assert(server.port === testPort)
      } finally {
        server.stop
      }
    }
  }
}
