package sc.ala.http.mock

import play.api.mvc._
import play.api.routing.Router.Routes
import play.core.server._

import HttpMock.implementedMethods

case class Setting(
  port    : Int = 0,
  methods : Set[String] = implementedMethods
) {
  validate()

  val logs = new AccessLogQueue()

  def routeFor(method: String): Routes = {
    import play.api.routing.sird._

    def action(r: RequestHeader) = Action { logs.add(r); Results.Ok(r.toString) }

    // TODO: add query parameters and request body to logs
    method match {
      case "GET"     => { case r@ GET    (p"/$path*") => action(r) }
      case "POST"    => { case r@ POST   (p"/$path*") => action(r) }
      case "PUT"     => { case r@ PUT    (p"/$path*") => action(r) }
      case "PATCH"   => { case r@ PATCH  (p"/$path*") => action(r) }
      case "DELETE"  => { case r@ DELETE (p"/$path*") => action(r) }
      case "HEAD"    => { case r@ HEAD   (p"/$path*") => action(r) }
      case "OPTIONS" => { case r@ OPTIONS(p"/$path*") => action(r) }
    }
  }

  def routes: Routes = methods.map(routeFor).foldLeft(PartialFunction.empty: Routes) {_ orElse _}

  private def validate(): Unit = {
    methods.diff(implementedMethods) match {
      case unknown if unknown.nonEmpty => s"unknown Methods $unknown"
      case _ =>
    }
  }
}

object Setting {
  implicit def toHttpMockDown(setting: Setting): HttpMockDown = HttpMockDown(setting, setting.port)
}
