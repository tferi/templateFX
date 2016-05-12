package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

import scala.language.existentials

abstract class NodeFixture[-Container] {
  def get(container: Container): Option[Node]

  def set(container: Container, node: Node): Unit

  def reconcile[SpecType  <: Node](container: Container, specOption: Option[Spec[SpecType]]): List[Change] = {
    get(container) -> specOption match {
      case (Some(existing), Some(specified)) if existing.getClass == specified.specifiedClass  =>
        specified.calculateMutation(existing.asInstanceOf[SpecType])

      case (None, None) =>
        Nil

      case _ =>
        List(SetFixture(container, this, specOption))
    }
  }
}
