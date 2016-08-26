package com.tothferenc.templateFX.specs

import java.lang.reflect.Constructor

class NoConstructorForParams(clazz: Class[_], params: Seq[Any])
  extends Exception(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")

object UniversalConstructor {
  def instantiate[Expected](clazz: Class[Expected], constructorParams: Seq[Object]): Expected = {
    val constructor: Constructor[Expected] =
      clazz.getConstructors.find { constructor =>
        constructor.getParameterCount == constructorParams.length && constructorParams.view.zipWithIndex.forall {
          case (param, index) => constructor.getParameterTypes()(index).isAssignableFrom(param.getClass)
        }
      }.getOrElse(throw new NoConstructorForParams(clazz, constructorParams)).asInstanceOf[Constructor[Expected]]
    try {
      constructor.newInstance(constructorParams: _*)
    } catch {
      case instantiation: InstantiationException => throw new Exception("Unable to instantiate abstract class: " + clazz.getSimpleName, instantiation)
    }
  }
}
