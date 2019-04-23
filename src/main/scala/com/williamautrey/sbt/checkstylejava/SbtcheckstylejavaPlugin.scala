package com.williamautrey.sbt.checkstylejava

import java.io.{FileOutputStream, OutputStream}
import java.util.Properties

import com.puppycrawl.tools.checkstyle.api.AuditListener
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

import scala.collection.JavaConverters._
import scala.sys.process._
import com.puppycrawl.tools.checkstyle._
import com.puppycrawl.tools.checkstyle.api.AutomaticBean.OutputStreamOptions
import com.sun.org.apache.xml.internal.serialize.OutputFormat

import scala.sys.process.Process

object SbtcheckstylejavaPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {
    val checkstyleConfig = settingKey[String]("Location of a style configuration.  Defaults to <resource dir>/google_checks.xml")
    val checkstyleTarget = settingKey[String]("XML file containing the checkstyle report.")
    val checkstyleFetchGoogle = taskKey[File]("Fetch checkstyle's Google style format from their git repository.")
    val checkstyle = taskKey[Int]("Use Java Checkstyle to analyze only the Java code in a project")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    checkstyleConfig := ((resourceDirectory in Compile).value / "google_checks.xml").getPath,
    checkstyleTarget := (target.value / "checkstyle-result.xml").getPath,
    checkstyleFetchGoogle := checkstyleFetchGoogleStyle().value,
    checkstyle := checkstyleProcess.value
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()

  private def relPath(file: File, base: File): File =
    file.relativeTo(base).getOrElse(file)

  // todo - Default to an internal config if checkstyleConfig is empty
  private def getConfig(): Def.Initialize[Task[String]] = Def.task {
    checkstyleConfig.value
  }

  // todo - Possible option to load properties file.
  // see http://checkstyle.sourceforge.net/config.html#Properties
  private def getProperties(): Properties = {
    System.getProperties
  }

  /**
    * Returns a listener that logs XML to a file corresponding to target, and logs normal output to a string
    * @param target Name/path of the file to contain output
    * @return
    */
  private def getListener(target: String, logger: Logger): AuditListener = {
    new SbtCheckstyleLogger(new FileOutputStream(target),OutputStreamOptions.CLOSE, logger)
  }

  /**
    * Fetches Google checkstyle format from checkstyle's public repo.  Puts it in src/main/resources/google_checks.xml
    */
  def checkstyleFetchGoogleStyle(): Def.Initialize[Task[File]] = Def.task {
    //fetch https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml
    val destination = (resourceDirectory in Compile).value / "google_checks.xml"
    destination #< url("https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml") !;
    destination
  }

  def checkstyleFetchSunStyle(): Def.Initialize[Task[File]] = Def.task{
    //fetch https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/sun_checks.xml
    val destination = (resourceDirectory in Compile).value / "sun_checks.xml"
    destination #< url("https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/sun_checks.xml") !;
    destination
  }

  /**
    * Gathers configs and runs checkstyle's Checker.process.
    * @return
    */
  private def checkstyleProcess(): Def.Initialize[Task[Int]] = Def.task {
    val log = streams.value.log
    val allSources = (sources in Compile).value
    val javaFiles = allSources.filter(_.getName endsWith ".java").asJava
    val javaSrcDir = (javaSource in Compile).value
    val baseDir = baseDirectory.value

    //initialize
    val checker = new Checker()
    log.info(s"Checkstyle version: ${checker.getClass.getPackage.getImplementationVersion}")

    //configure
    val config = ConfigurationLoader.loadConfiguration(getConfig().value, new PropertiesExpander(getProperties()))
    checker.setModuleClassLoader(classOf[Checker].getClassLoader)
    checker.configure(config)
    checker.addListener(getListener(checkstyleTarget.value, log))

    //run
    log.info(s"Running checkstyle: ${relPath(javaSrcDir, baseDir)}")
    val findings = checker.process(javaFiles)

    checker.destroy()

    findings
  }
}
