package com.williamautrey.sbt.checkstylejava

import java.io.OutputStream

import com.puppycrawl.tools.checkstyle.XMLLogger
import com.puppycrawl.tools.checkstyle.api.AutomaticBean.OutputStreamOptions
import com.puppycrawl.tools.checkstyle.api.{AuditEvent, AuditListener, SeverityLevel}
import sbt.util.Logger

/**
  * Default checkstyle logger.  Logs XML to a target stream, but also logs event details to console.
  * @param outputStream
  * @param outputStreamOptions
  * @param logger
  */
class SbtCheckstyleLogger(outputStream: OutputStream, outputStreamOptions: OutputStreamOptions, logger: Logger)
  extends XMLLogger(outputStream,outputStreamOptions) {

  def message(event: AuditEvent): String = s"${event.getFileName}:${event.getLine}: ${event.getMessage}"

  override def auditStarted(event: AuditEvent): Unit = {
    logger.info("Audit started")
    super.auditStarted(event)
  }

  override def auditFinished(event: AuditEvent): Unit = {
    logger.info("Audit completed")
    super.auditFinished(event)
  }

  override def fileStarted(event: AuditEvent): Unit = {
    super.fileStarted(event)
  }

  override def fileFinished(event: AuditEvent): Unit = {
    super.fileFinished(event)
  }

  override def addError(event: AuditEvent): Unit = {
    event.getSeverityLevel match {
      case SeverityLevel.ERROR =>
        logger.error(message(event))
      case SeverityLevel.WARNING =>
        logger.warn(message(event))
      case _ =>
    }

    super.addError(event)
  }

  override def addException(event: AuditEvent, throwable: Throwable): Unit = {
    logger.error(message(event))
    super.addException(event,throwable)
  }
}
