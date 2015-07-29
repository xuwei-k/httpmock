package sc.ala.http.mock
    
object HttpMock {
  val implementedMethods: Set[HttpMethod] = Set(GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS)

  // syntax sugar for Setting
  def start(): HttpMock = Setting().start()
  def start(port: Int): HttpMock = Setting(port).start()
  def run[A](action: HttpMockUp => A): Unit = Setting().run(action)
  def run[A](port: Int)(action: HttpMockUp => A): Unit = Setting(port).run(action)
}

trait HttpMock {
  def setting : Setting
  def start() : HttpMock
  def stop()  : HttpMock
  def run[A](action: HttpMockUp => A): Unit
  def port : Int
  def url  : String = s"http://127.0.0.1:$port/"
  def logs : Expectation = setting.logs
}
