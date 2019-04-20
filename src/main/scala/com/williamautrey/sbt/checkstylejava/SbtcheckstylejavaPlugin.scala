package com.williamautrey.sbt.checkstylejava

import java.io.{FileOutputStream, OutputStream}
import java.util.Properties

import com.puppycrawl.tools.checkstyle.api.AuditListener
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

import scala.collection.JavaConverters._
import com.puppycrawl.tools.checkstyle._
import com.puppycrawl.tools.checkstyle.api.AutomaticBean.OutputStreamOptions
import com.sun.org.apache.xml.internal.serialize.OutputFormat

object SbtcheckstylejavaPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {
    val checkstyleVersion = settingKey[String]("Version of checkstyle to import")
    val checkstyle = taskKey[Unit]("Use Java Checkstyle to analyze only the Java code in a project")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    checkstyleVersion := "8.19",
    checkstyle := checkstyleProcess.value
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()

  private def relPath(file: File, base: File): File =
    file.relativeTo(base).getOrElse(file)

  // todo - implement
  private def getConfig(): String = ""

  // todo - implement
  private def getProperties(): Properties = {
    System.getProperties
  }

  // todo - return NONE if stdout, CLOSE if a file.
  private def closeStream(): OutputStreamOptions = {
    OutputStreamOptions.NONE
  }

  // todo - XML to file, default to stdout
  private def getListener(): AuditListener = {
    //if(outDestination = stdout)
    //if xml
    //val outPath = ""
    //new XMLLogger(new FileOutputStream(outPath),closeStream())
    new DefaultLogger(System.out,closeStream())
  }

  def checkstyleFetchGoogleStyle(): Def.Initialize[Task[Boolean]] = Def.task {
    //fetch https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml
    false
  }

  def checkstyleFetchSunStyle(): Def.Initialize[Task[Boolean]] = Def.task{
    //fetch https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/sun_checks.xml
    false
  }

  private def checkstyleProcess(): Def.Initialize[Task[Unit]] = Def.task {
    val log = streams.value.log
    val allSources = (sources in Compile).value
    val javaFiles = allSources.filter(_.getName endsWith ".java").asJava
    val javaSrcDir = (javaSource in Compile).value
    val baseDir = baseDirectory.value

    //initialize
    val checker = new Checker()
    log.info(s"Checkstyle version: ${checker.getClass.getPackage.getImplementationVersion}")

    //configure
    val config = ConfigurationLoader.loadConfiguration(getConfig(), new PropertiesExpander(getProperties()))
    checker.configure(config)
    checker.addListener(getListener())
    checker.setModuleClassLoader(classOf[Checker].getClassLoader)

    log.info(s"Running checkstyle: ${relPath(javaSrcDir, baseDir)}")
    checker.process(javaFiles)
  }
}
