val Scala211 = "2.11.11"

crossScalaVersions := "2.12.3" :: Scala211 :: Nil
scalaVersion := Scala211

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

version := "0.3.3-SNAPSHOT"
organization := "com.github.xuwei-k"
name := "httpmock"
description := "Real http server for stubbing and expectations in Scala"
homepage := Some(url("https://github.com/xuwei-k/httpmock"))
licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

pomExtra := (
     <developers>
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
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-netty-server" % "2.6.2",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "com.ning" % "async-http-client" % "1.9.40" % "test"
)

fork in run := true
fork in Test := true
