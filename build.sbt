val Scala211 = "2.11.12"

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseCrossBuild := true

crossScalaVersions := "2.12.16" :: Scala211 :: Nil
scalaVersion := Scala211

val unusedWarnings = Seq("-Ywarn-unused")

scalacOptions ++= unusedWarnings
scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-language:postfixOps"
)

Seq(Compile, Test).flatMap(c => c / console / scalacOptions --= unusedWarnings)

organization := "com.github.xuwei-k"
name := "httpmock"
description := "Real http server for stubbing and expectations in Scala"
homepage := Some(url("https://github.com/xuwei-k/httpmock"))
licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

pomExtra := <developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
<scm>
  <url>https://github.com/xuwei-k/httpmock</url>
  <connection>scm:git:git@github.com:xuwei-k/httpmock.git</connection>
</scm>

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-netty-server" % "2.6.25",
  "org.scalatest" %% "scalatest-funspec" % "3.2.12" % "test",
  "com.ning" % "async-http-client" % "1.9.40" % "test"
)

run / fork := true
Test / fork := true
