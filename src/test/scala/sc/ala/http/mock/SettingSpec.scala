package sc.ala.http.mock

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
}
