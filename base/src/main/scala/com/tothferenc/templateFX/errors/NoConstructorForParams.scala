package com.tothferenc.templateFX.errors

final case class NoConstructorForParams[T](clazz: Class[T], params: Seq[Any])
  extends RuntimeException(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")
