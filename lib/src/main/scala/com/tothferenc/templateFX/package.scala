package com.tothferenc

import javafx.scene.Node
import javafx.scene.layout.Pane

package object templateFX {

  type NodeDef = Definition[_ <: Node]

  type TFXParent = Pane
}
