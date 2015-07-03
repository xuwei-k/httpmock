package sc.ala.http.mock
    
import java.util.concurrent.ConcurrentLinkedQueue
import play.api.mvc._

class AccessLogQueue() extends ConcurrentLinkedQueue[AccessLog] with Expectation {
  def add(request: RequestHeader): Unit = super.add(AccessLog(request))
  def add(request: RequestHeader, body: Option[Array[Byte]]): Unit = super.add(AccessLog(request, body))
  def shift(): Option[AccessLog] = Option(poll())
}
