package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object UserData {

  def get[ExpectedType](node: Node, key: String): Option[ExpectedType] = {
    if (node.getUserData == null) {
      None
    } else {
      val elementAtKey: Option[Any] = node.getUserData.asInstanceOf[mutable.Map[String, Any]].get(key)
      elementAtKey.map(_.asInstanceOf[ExpectedType])
    }
  }

  def set[Value](node: Node, key: String, value: Value): Unit = {
    if (node.getUserData == null) {
      val listMap = new mutable.ListMap[String, Any]()
      listMap += key -> value
      node.setUserData(listMap)
    } else {
      node.getUserData.asInstanceOf[mutable.Map[String, Any]] += key -> value
    }
  }
}

object ManagedAttributes {

  def get[FXType <: Node](node: FXType): Option[mutable.Set[RemovableFeature[FXType]]] = {
    UserData.get[mutable.Set[RemovableFeature[FXType]]](node, Attribute.key)
  }

  def set(node: Node, attributes: mutable.Set[RemovableFeature[_]]): Unit = {
    UserData.set(node, Attribute.key, attributes)
  }
}
