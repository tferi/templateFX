package com.tothferenc.templateFX.specs

import java.lang.reflect.Constructor

class NoConstructorForParams(clazz: Class[_], params: Seq[Any])
  extends Exception(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")

private object UniversalConstructor {
  def instantiate[Expected](constructorParams: Seq[Any])(implicit clazz: Class[Expected]): Expected = {
    val constructor: Constructor[Expected] =
      clazz.getConstructors.find { constructor =>
        constructor.getParameterCount == constructorParams.length && constructorParams.zipWithIndex.forall {
          case (param, index) => param.getClass == constructor.getParameterTypes()(index)
        }
      }.getOrElse(throw new NoConstructorForParams(clazz, constructorParams)).asInstanceOf[Constructor[Expected]]
    constructor.newInstance()
  }
}
