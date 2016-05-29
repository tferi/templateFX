package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.specs.Fixture

package object fixtures {

  case object ControlContextMenu extends Fixture[Control, ContextMenu] {
    override def set(container: Control, fixed: ContextMenu): Unit = container.setContextMenu(fixed)
    override def read(container: Control): ContextMenu = container.getContextMenu
    override def remove(item: Control): Unit = set(item, null)
  }

  case object ScrollPaneContent extends Fixture[ScrollPane, Node] {
    override def read(container: ScrollPane): Node = container.getContent
    override def set(container: ScrollPane, node: Node): Unit = container.setContent(node)
    override def remove(item: ScrollPane): Unit = set(item, null)
  }

  case object TabContent extends Fixture[Tab, Node] {
    override def read(container: Tab): Node = container.getContent
    override def set(container: Tab, node: Node): Unit = container.setContent(node)
    override def remove(item: Tab): Unit = set(item, null)
  }

  case object BorderTop extends Fixture[BorderPane, Node] {
    override def remove(item: BorderPane): Unit = set(item, null)
    override def set(container: BorderPane, fixed: Node): Unit = container.setTop(fixed)
    override def read(container: BorderPane): Node = container.getTop
  }
}
