package com.tothferenc.templateFX

import javafx.scene.Node

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Util {

  def getUserData[ExpectedType](node: Node, key: String): Option[ExpectedType] = {
    if (node.getUserData == null) {
      None
    } else {
      val elementAtKey: Option[Any] = node.getUserData.asInstanceOf[scala.collection.mutable.Map[String, Any]].get(key)
      elementAtKey.map(_.asInstanceOf[ExpectedType])
    }
  }

  def setUserData[Value](node: Node, key: String, value: Value): Unit = {
    if (node.getUserData == null) {
      val listMap = new mutable.ListMap[String, Any]()
      listMap += key -> value
      node.setUserData(listMap)
    } else {
      node.getUserData.asInstanceOf[scala.collection.mutable.Map[String, Any]] += key -> value
    }
  }

  def getManagedAttributes(node: Node): Option[ListBuffer[Unsettable[_]]] = {
    getUserData[ListBuffer[Unsettable[_]]](node, Attribute.key)
  }

  def setManagedAttributes(node: Node, attributes: ListBuffer[Unsettable[_]]): Unit = {
    setUserData(node, Attribute.key, attributes)
  }
}
