package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

import scala.language.existentials

abstract class NodeFixture[-Container] {
  def read(container: Container): Option[Node]

  def set(container: Container, node: Node): Unit

  def reconcile(container: Container, specOption: Option[Template[Node]]): List[Change] = {
    read(container) -> specOption match {
      case (Some(existing), Some(specified)) =>
        specified.reconcilationSteps(existing).getOrElse(List(SetFixture(container, this, specOption)))

      case (None, None) =>
        Nil

      case _ =>
        List(SetFixture(container, this, specOption))
    }
  }
}
