package com.tothferenc.templateFX.specs

import java.lang.reflect.Constructor

import com.tothferenc.templateFX.errors.NoConstructorForParams
import com.tothferenc.templateFX.errors.ConstructionFailed

import scala.util.control.NonFatal

object UniversalConstructor {
  def instantiate[Expected](clazz: Class[Expected], constructorParams: Seq[Object]): Expected = {
    val constructor: Constructor[Expected] =
      clazz.getConstructors.find { constructor =>
        constructor.getParameterCount == constructorParams.length && constructorParams.view.zipWithIndex.forall {
          case (param, index) => constructor.getParameterTypes()(index).isAssignableFrom(param.getClass)
        }
      }.getOrElse(throw NoConstructorForParams(clazz, constructorParams)).asInstanceOf[Constructor[Expected]]
    try {
      constructor.newInstance(constructorParams: _*)
    } catch {
      case NonFatal(t) => throw ConstructionFailed(clazz, t)
    }
  }
}
