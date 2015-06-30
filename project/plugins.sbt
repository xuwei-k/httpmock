scalacOptions ++= Seq("-deprecation", "-unchecked")

updateOptions ~= {_.withCachedResolution(true)}

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.4.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

