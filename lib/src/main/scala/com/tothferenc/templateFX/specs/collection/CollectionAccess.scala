package com.tothferenc.templateFX.specs.collection

abstract class CollectionAccess[-Container, Item] {
  def getCollection(container: Container): java.util.List[Item]
}