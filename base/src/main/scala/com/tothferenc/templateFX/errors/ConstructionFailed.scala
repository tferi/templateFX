package com.tothferenc.templateFX.errors

final case class ConstructionFailed[T](clazz: Class[T], cause: Throwable) extends RuntimeException("Unable to instantiate abstract class: " + clazz.getSimpleName, cause)
