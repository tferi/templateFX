package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.base.Fixture

package object fixtures {

  implicit val contextMenu = Fixture.simple[Control, ContextMenu]("ContextMenu", null)

  implicit val scrollPaneContent = Fixture.simple[ScrollPane, Node]("Content", null)

  implicit val tabContent = Fixture.simple[Tab, Node]("Content", null)

  implicit val borderTop = Fixture.simple[BorderPane, Node]("Top", null)
  implicit val borderRight = Fixture.simple[BorderPane, Node]("Right", null)
  implicit val borderBottom = Fixture.simple[BorderPane, Node]("Bottom", null)
  implicit val borderLeft = Fixture.simple[BorderPane, Node]("Left", null)
  implicit val borderCenter = Fixture.simple[BorderPane, Node]("Center", null)
}
