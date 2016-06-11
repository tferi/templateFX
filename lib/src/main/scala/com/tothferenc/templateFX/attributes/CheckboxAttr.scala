package com.tothferenc.templateFX.attributes

import javafx.scene.control.CheckBox

import com.tothferenc.templateFX.base.Attribute

object CheckboxAttr {
  val selected = Attribute.simple[CheckBox, Boolean]("Selected", false)
  val indeterminate = Attribute.simple[CheckBox, Boolean]("Indeterminate", false)
}
