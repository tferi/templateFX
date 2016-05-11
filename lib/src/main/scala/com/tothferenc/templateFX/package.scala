package com.tothferenc

import javafx.scene.Node
import javafx.scene.layout.Pane

package object templateFX {

  type NodeSpec = Spec[_ <: Node]

  type TFXParent = Pane
}
