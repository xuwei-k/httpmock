package sc.ala.http.mock

sealed abstract class HttpMethod(val value: String) extends Product with Serializable
case object GET extends HttpMethod("GET")
case object POST extends HttpMethod("POST")
case object PUT extends HttpMethod("PUT")
case object PATCH extends HttpMethod("PATCH")
case object DELETE extends HttpMethod("DELETE")
case object HEAD extends HttpMethod("HEAD")
case object OPTIONS extends HttpMethod("OPTIONS")
