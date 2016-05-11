package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }
import com.tothferenc.templateFX.{ Change, _ }

import scala.collection.mutable.ListBuffer

abstract class Spec[FXType <: Node] {
  implicit def specifiedClass: Class[FXType]
  def constraints: Seq[Constraint[FXType]]
  def materialize(): FXType
  def children: ChildrenSpec
  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change]

  def calculateMutation(nodeOfSameType: FXType): List[Change] = {
    val featuresToRemove = UserData.get[ListBuffer[RemovableFeature[FXType]]](nodeOfSameType, Attribute.key) match {
      case Some(features) =>
        features.filterNot { checked =>
          constraints.exists(_.attribute == checked)
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

  def materialize(): FXType = {
    val instance = UniversalConstructor.instantiate[FXType](constructorParams)
    val setting = Mutation(instance, constraints.flatMap(_(instance)), Nil)
    setting.execute()
    instance match {
      case container: TFXParent =>
        container.getChildren.addAll(children.materializeAll(): _*)
      case _ =>
        ()
    }
    instance
  }
}
