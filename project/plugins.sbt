scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.1")

updateOptions ~= {_.withCachedResolution(true)}

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.4.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

