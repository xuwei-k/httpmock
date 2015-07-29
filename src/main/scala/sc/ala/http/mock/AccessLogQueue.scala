package sc.ala.http.mock
    
import java.util.concurrent.ConcurrentLinkedQueue
import play.api.mvc._

final case class AccessLogQueue(queue: ConcurrentLinkedQueue[AccessLog] = new ConcurrentLinkedQueue[AccessLog]()) {
  def add(request: RequestHeader): Unit = queue.add(AccessLog(request))
  def add(request: RequestHeader, body: Option[ArrayByte]): Unit = queue.add(AccessLog(request, body))
  def shift(): Option[AccessLog] = Option(queue.poll())

  def filter(p: AccessLog => Boolean): Seq[AccessLog] = {
    val array = scala.collection.mutable.ArrayBuffer[AccessLog]()
    val it = queue.iterator()
    while (it.hasNext) {
      val log = it.next()
      if (p(log))
        array += log
    }
    array.toSeq
  }
}
