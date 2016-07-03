package com.tothferenc.templateFX.base

import org.slf4j.LoggerFactory

abstract class Change {

  def execute(): Unit = {
    if (Change.debug) Change.logger.debug(this.toString)
    exec()
  }

  protected def exec(): Unit
}

object Change {
  lazy val logger = LoggerFactory.getLogger("CHANGELOG")

  val debug: Boolean = java.lang.Boolean.getBoolean("tfx-debug")
}