package httpmock

import play.core.server.NettyServer

case class HttpMockUp(setting: Setting, netty: NettyServer) extends HttpMock {
  def port: Int = netty.httpPort.get // port must exist cause we set it and server is running

  def start(): HttpMock = this
  def stop(): HttpMock = { netty.stop(); HttpMockDown(setting, port) }
  def run[A](action: HttpMockUp => A): Unit =
    try {
      action(this)
    } finally {
      stop()
    }
}
