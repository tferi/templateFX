package com.tothferenc.templateFX.specs

import javafx.embed.swing.JFXPanel
import javafx.scene.control.Label

import org.specs2.mutable.Specification

import scala.util.Try

class UniversalConstructorSpec extends Specification {

  new JFXPanel()

  "UniversalConstrutor" should {

    "instantiate parameterless classes" in {
      Try(UniversalConstructor.instantiate(classOf[Label], Nil)) must beSuccessfulTry[Label]
    }

    "instantiate classes if there's an appropriate constructor available for the nonEmpty param list" in {
      Try(UniversalConstructor.instantiate(classOf[Label], List("Hello"))) must beSuccessfulTry[Label]
    }

    "fail to instantiate if an appropriate constructor is not available" in {
      Try(UniversalConstructor.instantiate(classOf[Label], List(None))) must beFailedTry[Label]
    }
  }
}
