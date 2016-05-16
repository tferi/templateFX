package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

abstract class Spec[FXType <: Node] {
  implicit def specifiedClass: Class[FXType]
  def constraints: Seq[Constraint[FXType]]
  def materialize(): FXType
  def reconcileChildren(node: FXType): List[Change]
  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change]

  def calculateMutation(node: Node): List[Change] = {
    val nodeOfSameType = node.asInstanceOf[FXType]
    val featuresToRemove = ManagedAttributes.get(nodeOfSameType) match {
      case Some(features) =>
        features.filterNot { checked =>
          constraints.exists(_.feature == checked)
        }
      case _ =>
        Nil
    }

    val featureUpdates = constraints.flatMap(_.apply(nodeOfSameType))

    (if (featureUpdates.nonEmpty || featuresToRemove.nonEmpty)
      List(Mutation[FXType](nodeOfSameType, featureUpdates, featuresToRemove))
    else
      Nil) ::: reconcileChildren(nodeOfSameType)
  }
}

abstract class ReflectiveSpec[FXType <: Node] extends Spec[FXType] {

  protected def constructorParams: Seq[Any]

  def initNodesBelow(instance: FXType): Unit

  def materialize(): FXType = {
    val instance = UniversalConstructor.instantiate[FXType](constructorParams)
    Mutation(instance, constraints.flatMap(_(instance)), Nil).execute()
    initNodesBelow(instance)
    instance
  }
}
