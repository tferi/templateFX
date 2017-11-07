package com.tothferenc.templateFX.errors

final case class DuplicateKeyException(key: String) extends RuntimeException(s"Multiple elements in sequence with the same key: $key")
