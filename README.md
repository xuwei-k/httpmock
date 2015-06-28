# HttpMock

Real http server for stubbing and expectations in Scala

Features
========

- Stubbing HTTP requests at real Http Server
- Response always OK for any methods and paths
  - supported methods: GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS

Usage
=====

#### start (random port)

```scala
import sc.ala.http.mock._
val server = HttpMock.start
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
val stopped   = server.stop           // GET: error
val restarted = stopped.start         // GET: ok
```

#### loan pattern

- `run` ensures `server.stop` after action

```scala
HttpMock.run       { server => ... }
HttpMock.run(port) { server => ... }
Setting(port).run  { server => ... }
```

#### restrict methods

- default: accept all methods

```scala
val server = Setting(methods = Set(GET, POST)).start
/*
  GET  => 200
  POST => 200
  PUT  => 404
*/
```
