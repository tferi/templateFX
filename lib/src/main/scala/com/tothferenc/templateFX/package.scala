package com.tothferenc

import javafx.scene.Node
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.specs.base.ClassAwareSpec

package object templateFX {

  type NodeSpec = ClassAwareSpec[_ <: Node]

  type TFXParent = Pane
}
