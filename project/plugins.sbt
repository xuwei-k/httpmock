scalacOptions ++= Seq("-deprecation", "-unchecked")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.6.8")
