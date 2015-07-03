package sc.ala.http.mock

import org.scalatest.{BeforeAndAfterEach, FunSpec}
import com.ning.http.client._
import scala.collection.mutable

import collection.JavaConversions._

trait TestHelper extends FunSpec with BeforeAndAfterEach with TestConfig {
  import java.util.concurrent.{Future => JavaFuture, TimeUnit}

  private val runningHttpMocks = mutable.Set[HttpMock]()

  protected def autoStop(server: HttpMock): HttpMock = {
    runningHttpMocks += server
    server
  }

  protected override def beforeEach(): Unit = {
    super.beforeEach()
    runningHttpMocks.clear()
  }

  protected override def afterEach(): Unit = {
    runningHttpMocks.foreach(_.stop())
    runningHttpMocks.clear()
    super.afterEach()
  }

  def start[A](setting: Setting)(action: HttpMock => A): Unit = {
    val server = setting.start
    try { action(server) } finally { server.stop }
  }

  def get(url: String): Response = {
    val f: JavaFuture[Response] = newAsyncHttpClient.prepareGet(url).execute()
    f.get(requestTimeoutMs + 500, TimeUnit.MILLISECONDS)
  }

  def post(url: String, body: String, headers: Map[String, String] = Map[String, String]()): Response = {
    val map = new FluentCaseInsensitiveStringsMap()
    for ((k,v) <- headers) map.add(k, v)
    val f: JavaFuture[Response] = newAsyncHttpClient.preparePost(url).setBody(body).setHeaders(map).execute()
    f.get(requestTimeoutMs + 500, TimeUnit.MILLISECONDS)
  }

  protected def newAsyncHttpClient: AsyncHttpClient = {
    val config = new AsyncHttpClientConfig.Builder()
      .setRequestTimeout(requestTimeoutMs)
      .build()
    new AsyncHttpClient(config)
  }
}
