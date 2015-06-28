package sc.ala.http.mock

import play.api.Mode
import play.core.server.{NettyServer, ServerConfig}

case class HttpMockDown(setting: Setting, port: Int) extends HttpMock {
  def start(): HttpMock = new HttpMockUp(setting, startNetty())
  def stop(): HttpMock = this
  def run[A](action: HttpMockUp => A): Unit = start().run(action)

  private def startNetty(): NettyServer = {
    val config = ServerConfig(mode = Mode.Test, port = Some(port))
    NettyServer.fromRouter(config)(setting.routes)
  }
}
