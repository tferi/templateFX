package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.base.Attribute

package object fixtures {

  implicit val contextMenu = Attribute.simple[Control, ContextMenu]("ContextMenu", null)

  implicit val scrollPaneContent = Attribute.simple[ScrollPane, Node]("Content", null)

  implicit val tabContent = Attribute.simple[Tab, Node]("Content", null)

  implicit val borderTop = Attribute.simple[BorderPane, Node]("Top", null)
  implicit val borderRight = Attribute.simple[BorderPane, Node]("Right", null)
  implicit val borderBottom = Attribute.simple[BorderPane, Node]("Bottom", null)
  implicit val borderLeft = Attribute.simple[BorderPane, Node]("Left", null)
  implicit val borderCenter = Attribute.simple[BorderPane, Node]("Center", null)
}
