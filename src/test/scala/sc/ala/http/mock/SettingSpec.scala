package sc.ala.http.mock

import play.api.mvc.{RequestHeader, Result, Results}

class SettingSpec extends TestHelper {
  describe("Setting()") {
    it("can start http service on some port") {
      val setting = Setting()
      start(setting){ server =>
        assert(server.port > 0)
      }
    }
  }

  describe("Setting(methods = Set(GET))") {
    val setting = Setting(methods = Set(GET))
    it("accepts GET [200]") {
      start(setting) { server =>
        assert(get(server.url).getStatusCode === 200)
      }
    }

    it("rejects POST [404]") {
      start(setting) { server =>
        assert(post(server.url, "body").getStatusCode === 404)
      }
    }
  }

  describe("Setting(handler = ???") {
    val handler: PartialFunction[RequestHeader, Result] = {
      case h: RequestHeader if h.method == "POST" => Results.Unauthorized
      case h: RequestHeader if h.uri.contains("rerorero") => Results.InternalServerError
    }

    it("responses matched handler results") {
      start(Setting(methods = Set(GET, POST), handler = handler)) { server =>
        assert(post(server.url, "body").getStatusCode === 401)
        assert(get(server.url + "rerorero").getStatusCode === 500)
      }
    }

    it("responses according to methods property if not matched") {
      start(Setting(methods = Set(GET), handler = handler)) { server =>
        assert(post(server.url, "body").getStatusCode === 404)
        assert(get(server.url + "rorerore").getStatusCode === 200)
      }
    }
  }
}
