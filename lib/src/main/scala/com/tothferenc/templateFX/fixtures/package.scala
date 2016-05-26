package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab

import com.tothferenc.templateFX.specs.Fixture

package object fixtures {

	case object ControlContextMenu extends Fixture[Control, ContextMenu] {
		override def set(container: Control, fixed: ContextMenu): Unit = container.setContextMenu(fixed)
		override def read(container: Control): Option[ContextMenu] = Option(container.getContextMenu)
		override def default: ContextMenu = null
	}

	case object ScrollPaneContent extends Fixture[ScrollPane, Node] {
		override def read(container: ScrollPane): Option[Node] = Option(container.getContent)
		override def set(container: ScrollPane, node: Node): Unit = container.setContent(node)
		override def default: Node = null
	}

	case object TabContent extends Fixture[Tab, Node] {
		override def read(container: Tab): Option[Node] = Option(container.getContent)
		override def set(container: Tab, node: Node): Unit = container.setContent(node)
		override def default: Node = null
	}
}
