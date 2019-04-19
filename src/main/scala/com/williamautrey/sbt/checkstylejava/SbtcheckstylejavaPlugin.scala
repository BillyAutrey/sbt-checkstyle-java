package com.williamautrey.sbt.checkstylejava

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object SbtcheckstylejavaPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {
    val checkstyleVersion = settingKey[String]("Version of checkstyle to import")
    val checkstyle = taskKey[String]("Use Java Checkstyle to analyze only the Java code in a project")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    checkstyleVersion := "8.19",
    checkstyle := checkstyleVersion.value
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}
