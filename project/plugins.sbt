scalacOptions ++= Seq("-deprecation", "-unchecked")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.2")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.6.0-RC4")
