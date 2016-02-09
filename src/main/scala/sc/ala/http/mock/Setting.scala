package sc.ala.http.mock

import play.api.http.HttpVerbs
import play.api.mvc._
import play.api.routing.Router.Routes

import HttpMock.implementedMethods

final case class Setting(
  port    : Int = 0,
  methods : Set[HttpMethod] = implementedMethods,
  handler : PartialFunction[RequestHeader, Result] = PartialFunction.empty
) {

  val logs = new AccessLogQueue()

  private[this] def routeFor(method: HttpMethod): Routes = {
    import play.api.routing.sird
    import play.api.routing.sird.UrlContext

    def action(r: RequestHeader) = Action { request =>
      if (r.method == HttpVerbs.POST || r.method == HttpVerbs.PUT) {
        request.body match {
          case AnyContentAsRaw(raw) => logs.add(r, raw.asBytes().map(ArrayByte))
          case _ => logs.add(r)
        }
      } else {
        logs.add(r)
      }
      handler.applyOrElse[RequestHeader, Result](r, r => Results.Ok(r.toString))
    }

    // TODO: add query parameters and request body to logs
    method match {
      case GET     => { case r@ sird.GET    (p"/$path*") => action(r) }
      case POST    => { case r@ sird.POST   (p"/$path*") => action(r) }
      case PUT     => { case r@ sird.PUT    (p"/$path*") => action(r) }
      case PATCH   => { case r@ sird.PATCH  (p"/$path*") => action(r) }
      case DELETE  => { case r@ sird.DELETE (p"/$path*") => action(r) }
      case HEAD    => { case r@ sird.HEAD   (p"/$path*") => action(r) }
      case OPTIONS => { case r@ sird.OPTIONS(p"/$path*") => action(r) }
    }
  }

  def routes: Routes = methods.map(routeFor).foldLeft(PartialFunction.empty: Routes) {_ orElse _}

}

object Setting {
  implicit def toHttpMockDown(setting: Setting): HttpMockDown = HttpMockDown(setting, setting.port)
}
