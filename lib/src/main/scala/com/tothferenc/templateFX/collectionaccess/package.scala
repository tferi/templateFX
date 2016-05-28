package com.tothferenc.templateFX

import java.util
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane

package object collectionaccess {

  class PaneNodesAccess extends CollectionAccess[Pane, Node] {
    override def getCollection(container: Pane): ObservableList[Node] = container.getChildren
  }

  class TabPaneTabsAccess extends CollectionAccess[TabPane, Tab] {
    override def getCollection(container: TabPane): ObservableList[Tab] = container.getTabs
  }

  class ContextMenuMenuItemsAccess extends CollectionAccess[ContextMenu, MenuItem] {
    override def getCollection(container: ContextMenu): util.List[MenuItem] = container.getItems
  }
}
