package sc.ala.http.mock
    
import java.util.concurrent.ConcurrentLinkedQueue
import play.api.mvc._

class AccessLogQueue() extends ConcurrentLinkedQueue[AccessLog] with Expectation {
  def add(request: RequestHeader): Unit = super.add(AccessLog(request))
  def add(request: RequestHeader, body: Option[ArrayByte]): Unit = super.add(AccessLog(request, body))
  def shift(): Option[AccessLog] = Option(poll())

  def filter(p: AccessLog => Boolean): Seq[AccessLog] = {
    val array = scala.collection.mutable.ArrayBuffer[AccessLog]()
    val it = this.iterator()
    while (it.hasNext) {
      val log = it.next()
      if (p(log))
        array += log
    }
    array.toSeq
  }
}
