package com.tothferenc.templateFX.specs

import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext
import scala.util.Try

class UniversalConstructorSpec extends Specification {

  "UniversalConstrutor" should {

    "instantiate parameterless classes" in {
      Try(UniversalConstructor.instantiate(classOf[StringBuilder], Nil)) must beSuccessfulTry[StringBuilder]
    }

    "instantiate classes if there's an appropriate constructor available for the nonEmpty param list" in {
      Try(UniversalConstructor.instantiate(classOf[Exception], List("Exception message"))) must beSuccessfulTry[Exception]
    }

    "fail to instantiate if an appropriate constructor is not available" in {
      Try(UniversalConstructor.instantiate(classOf[ExecutionContext], List(None))) must beFailedTry[ExecutionContext]
    }
  }
}
