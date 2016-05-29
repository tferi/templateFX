package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab

import scala.collection.mutable

package object userdata {

  implicit val contextMenuUserDataAccess = new UserDataAccess[ContextMenu] {
    override def init(container: ContextMenu): mutable.Map[String, Any] = {
      val listMap: mutable.ListMap[String, Any] = new mutable.ListMap[String, Any]()
      container.setUserData(listMap)
      listMap
    }

    override def get(container: ContextMenu): Option[mutable.Map[String, Any]] =
      Option(container.getUserData).map(_.asInstanceOf[mutable.Map[String, Any]])
  }

  implicit val menuItemUserDataAccess = new UserDataAccess[MenuItem] {
    override def init(container: MenuItem): mutable.Map[String, Any] = {
      val listMap: mutable.ListMap[String, Any] = new mutable.ListMap[String, Any]()
      container.setUserData(listMap)
      listMap
    }

    override def get(container: MenuItem): Option[mutable.Map[String, Any]] =
      Option(container.getUserData).map(_.asInstanceOf[mutable.Map[String, Any]])
  }

  implicit val nodeUserDataAccess = new UserDataAccess[Node] {
    override def init(container: Node): mutable.Map[String, Any] = {
      val listMap: mutable.ListMap[String, Any] = new mutable.ListMap[String, Any]()
      container.setUserData(listMap)
      listMap
    }

    override def get(container: Node): Option[mutable.Map[String, Any]] =
      Option(container.getUserData).map(_.asInstanceOf[mutable.Map[String, Any]])
  }

  implicit val tabUserDataAccess = new UserDataAccess[Tab] {
    override def init(container: Tab): mutable.Map[String, Any] = {
      val listMap: mutable.ListMap[String, Any] = new mutable.ListMap[String, Any]()
      container.setUserData(listMap)
      listMap
    }

    override def get(container: Tab): Option[mutable.Map[String, Any]] =
      Option(container.getUserData).map(_.asInstanceOf[mutable.Map[String, Any]])
  }
}
