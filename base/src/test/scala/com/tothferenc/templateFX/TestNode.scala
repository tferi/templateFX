package com.tothferenc.templateFX

import com.tothferenc.templateFX.base.attribute.Attribute

import scala.collection.mutable

final case class StringAttribute(name: String) extends Attribute[TestNode, String] {
  override def read(src: TestNode): String = src.getAttr(name)

  override def set(target: TestNode, value: String): Unit = target.setAttr(name, value)

  override def remove(item: TestNode): Unit = item.remove(name)
}

final case class InvalidAttribute(name: String, valid: Set[String]) extends Exception(s"$name is not a valid attribute. Valid attributes are :${valid.mkString(", ")}")

class TestNode(attributes: Set[StringAttribute]) {

  val m: mutable.HashMap[String, String] = new mutable.HashMap()

  private val attributeNames = attributes.map(_.name)

  private def withValidAttribute[T](name: String)(block: => T) = {
    if (attributeNames.contains(name)) {
      block
    } else {
      throw InvalidAttribute(name, attributeNames)
    }
  }

  def setAttr(name: String, value: String): Unit = {
    withValidAttribute(name) {
      m.update(name, value)
    }
  }

  def getAttr(name: String): String = {
    withValidAttribute(name) {
      m.get(name).orNull
    }
  }

  def remove(name: String) = {
    withValidAttribute(name) {
      m.remove(name)
    }
  }

  def currentState: Map[StringAttribute, String] = {
    m.toMap.map {
      case (name, value) => attributes.find(_.name == name).get -> value
    }
  }
}
