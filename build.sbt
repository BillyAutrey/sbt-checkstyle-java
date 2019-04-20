scalaVersion := "2.12.8"

name := "sbt-checkstyle-java"
version := "0.1-SNAPSHOT"
organization := "com.williamautrey"
description := "SBT Plugin to run checkstyle on Java code"
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

enablePlugins(GitVersioning,SbtPlugin)

sbtPlugin := true
publishMavenStyle := false
bintrayRepository := "sbt-plugins"
bintrayOrganization in bintray := None
bintrayPackageLabels := Seq("sbt","plugin")

//dependencies
libraryDependencies += "com.puppycrawl.tools" % "checkstyle" % "8.19"

//tests
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

initialCommands in console := """import com.williamautrey.sbt.checkstylejava._"""

// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++= 
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
scriptedBufferLog := false