package sc.ala.http.mock

trait TestConfig {
  protected def testPort         : Int = 2080  // pray this port is not used
  protected def requestTimeoutMs : Int = 1000
}
