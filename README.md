# HttpMock

Real http server for stubbing and expectations in Scala

Features
========

- Stubbing HTTP requests at real Http Server
- Response always OK for any methods and paths
  - supported methods: GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS

Usage
=====

```
import sc.ala.http.mock._
val server = HttpMock.start
server.port  // => 37781 (automatically set by default)
// send requests to "http://127.0.0.1:37781" (or server.url)
server.stop
```
