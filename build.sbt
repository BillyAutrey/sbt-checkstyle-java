scalaVersion := "2.12.8"

ThisBuild / version := "0.1"
ThisBuild / organization := "com.williamautrey"
ThisBuild / description := "SBT Plugin to run checkstyle on Java code"
ThisBuild / licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    sbtPlugin := true,
    name := "sbt-checkstyle-java",
    publishMavenStyle := false,
    bintrayRepository := "sbt-plugins",
    bintrayOrganization in bintray := None
  )