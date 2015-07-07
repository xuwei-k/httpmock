package sc.ala.http.mock
    
import play.api.mvc.RequestHeader

final case class AccessLog(
  request : RequestHeader,
  bodyOpt : Option[Array[Byte]] = None
) {

  override def equals(other: Any): Boolean = other match {
    case that: AccessLog =>
      this.===(that)
    case _ =>
      false
  }

  def ===(that: AccessLog): Boolean = (this.bodyOpt, that.bodyOpt) match {
    case (Some(body1), Some(body2)) =>
      (this.request == that.request) && java.util.Arrays.equals(body1, body2)
    case (None, None) =>
      this.request == that.request
    case _ =>
      false
  }

  override def hashCode = {
    (request.hashCode() * 31) + bodyOpt.map(java.util.Arrays.hashCode(_)).getOrElse(7)
  }

}
