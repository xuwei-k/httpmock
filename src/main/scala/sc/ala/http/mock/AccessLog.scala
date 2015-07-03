package sc.ala.http.mock
    
import play.api.mvc.RequestHeader

case class AccessLog(
  request : RequestHeader,
  bodyOpt : Option[Array[Byte]] = None
)
