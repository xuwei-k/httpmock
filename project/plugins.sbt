scalacOptions ++= Seq("-deprecation", "-unchecked")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.8")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")
