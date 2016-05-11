package com.tothferenc

import javafx.scene.Node
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.specs.Spec

package object templateFX {

  type NodeSpec = Spec[_ <: Node]

  type TFXParent = Pane
}
