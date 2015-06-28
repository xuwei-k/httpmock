package sc.ala.http.mock

import play.core.server._

case class HttpMockUp(setting: Setting, netty: NettyServer) extends HttpMock {
  def port: Int = netty.httpPort.get  // port must exist cause we set it and server is running

  def start(): HttpMock = this
  def stop() : HttpMock = { netty.stop(); HttpMockDown(setting, port) }
  def run[A](action: HttpMockUp => A): Unit = try { action(this) } finally { stop() }
}
