[![Build Status](https://travis-ci.org/xuwei-k/httpmock.svg?branch=master)](https://travis-ci.org/xuwei-k/httpmock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.xuwei-k/httpmock_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.xuwei-k/httpmock_2.12)
[![scaladoc](http://javadoc-badge.appspot.com/com.github.xuwei-k/httpmock_2.12.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.github.xuwei-k/httpmock_2.12/httpmock/index.html?javadocio=true)


# HttpMock

Real http server for stubbing and expectations in Scala

Features
========

- Stubbing HTTP requests at real Http Server
  - responds always OK for any methods and paths
  - supported methods: GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
- Expecting HTTP requests as AccessLog
  - asserts AccessLog about methods and counts

Stubbing
========

#### start (random port)

```scala
import httpmock._
val server = HttpMock.start()
server.port  // => 37781 (automatically set by default)
// send requests to "http://127.0.0.1:37781" (or server.url)
server.stop
```

#### start with port

```scala
val server = HttpMock.start(9000)
server.port  // => 9000
```

#### restartable

- HttpMock is immutable

```scala
val server    = HttpMock.start(9000)  // GET: ok
val stopped   = server.stop()         // GET: error
val restarted = stopped.start()       // GET: ok
```

#### loan pattern

- `run()` ensures `server.stop()` after action

```scala
HttpMock.run()      { server => ... }
HttpMock.run(port)  { server => ... }
Setting(port).run() { server => ... }
```

#### restrict methods

- default: accept all methods

```scala
val server = Setting(methods = Set(GET, POST)).start()
/*
  GET  => 200
  POST => 200
  PUT  => 404
*/
```

### customize request handler

```
val server = Setting(handler = {
    case h: RequestHeader if h.version == "HTTP/1.0" => Results.HttpVersionNotSupported
})
/*
  GET  => 200
  GET with HTTP/1.0 => 505
*/
```

Expectations
============

- implemented expectations:
  - method, body, header, count

```scala
import httpmock._
import scala.concurrent.duration._

val server = HttpMock.start(9000)
```

#### assert methods and counts

```shell
curl http://127.0.0.1:9000/
```

```scala
server.logs.expect(GET , count = 1)(1.second)  // (PASS)
server.logs.expect(GET , count = 2)(1.second)  // java.lang.AssertionError
server.logs.expect(POST, count = 1)(1.second)  // java.lang.AssertionError

server.stop()
```

#### assert methods and body and headers

```shell
curl -X POST -H "Content-type: application/octet-stream" http://127.0.0.1:9000/ -d foo
curl -X POST -H "Content-type: application/octet-stream" http://127.0.0.1:9000/ -d bar
curl -X POST -H "Content-type: application/octet-stream" -H "X-ID: 1" http://127.0.0.1:9000/ -d bar
```

```scala
server.logs.expect(POST).body("foo")(1.second)           // (PASS)
server.logs.expect(POST).body("bar")(1.second)           // java.lang.AssertionError
server.logs.expect(POST).body("bar").count(2)(1.second)  // (PASS)
server.logs.expect(POST).body("baz")(1.second)           // java.lang.AssertionError
server.logs.expect(POST).header("X-ID", "1")(1.second)   // (PASS)

server.stop()
```

#### assert bodies as Set

```shell
curl -X POST -H "Content-type: application/octet-stream" http://127.0.0.1:9000/ -d foo
curl -X POST -H "Content-type: application/octet-stream" http://127.0.0.1:9000/ -d bar
curl -X POST -H "Content-type: application/octet-stream" -H "X-ID: 1" http://127.0.0.1:9000/ -d bar
```

```scala
server.logs.expect(POST).bodies(Set("foo", "bar"))(1.second)  // (PASS)
server.logs.expect(POST).bodies(Set("bar", "foo"))(1.second)  // (PASS)
server.logs.expect(POST).bodies(Set("foo", "XXX"))(1.second)  // java.lang.AssertionError

server.stop()
```

#### using in Spec

```scala
import httpmock._
import scala.concurrent.duration._
import org.scalatest.FunSpec

class FooSpec extends FunSpec {
  describe("foo") {
    it("test with real httpd") {
      HttpMock.run { server =>
        // your application logic to `server.url`
        ...

        // assert your requests like this
        server.logs.expect(POST, count = 2)(3.seconds)
      }
    }
  }
}
```
