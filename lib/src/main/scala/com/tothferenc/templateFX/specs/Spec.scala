package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

abstract class Template {
  type Output
  def materialize(): Output
  def calculateMutation(otherItem: Node): List[Change]
}

object Template {
  type Aux[Out] = Template { type Output = Out}
}

abstract class Spec[FXType <: Node] extends Template {

  type Output = FXType

  implicit def specifiedClass: Class[FXType]
  def constraints: Seq[Constraint[FXType]]
  def materialize(): FXType
  def children: ChildrenSpec
  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change]

  override def calculateMutation(otherItem: Node): List[Change] = {
    val nodeOfSameType = otherItem.asInstanceOf[FXType]
    val featuresToRemove = ManagedAttributes.get(nodeOfSameType) match {
      case Some(features) =>
        features.filterNot { checked =>
          constraints.exists(_.feature == checked)
        }
      case _ =>
        Nil
    }

    val featureUpdates = constraints.flatMap(_.apply(nodeOfSameType))

    val mutation = if (featureUpdates.nonEmpty || featuresToRemove.nonEmpty)
      List(Mutation[FXType](nodeOfSameType, featureUpdates, featuresToRemove))
    else
      Nil

    mutation ::: {
      nodeOfSameType match {
        case container: TFXParent =>
          children.requiredChangesIn(container)
        case leaf =>
          Nil
      }
    }
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
