import sbt.Keys._

scalaVersion := "2.11.7"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

xerial.sbt.Sonatype.sonatypeRootSettings

// Maven Publishing
// http://www.scala-sbt.org/0.13/docs/Using-Sonatype.html

publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

version := "0.3.0"  // "0.4-SNAPSHOT"
organization := "sc.ala"
name := "http-mock"
description := "Real http server for stubbing and expectations in Scala"
homepage := Some(url("https://github.com/maiha/http-mock"))
licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

pomExtra := (
     <developers>
        <developer>
          <id>maiha</id>
          <name>Kazunori Nishi</name>
          <url>https://github.com/maiha</url>
        </developer>
      </developers>
      <scm>
        <url>https://github.com/maiha/http-mock</url>
        <connection>scm:git:git@github.com:maiha/http-mock.git</connection>
      </scm>
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-netty-server" % "2.4.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.ning" % "async-http-client" % "1.9.29" % "test"
)

fork in run := true
fork in Test := true
