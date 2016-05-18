package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.userdata.ManagedAttributes
import com.tothferenc.templateFX.userdata.UserDataAccess

abstract class Template[T] {
  def materialize(): T
  def reconcilationSteps(other: Node): Option[List[Change]]
}

abstract class ConstraintBasedReconcilation[T] extends Template[T] {

  implicit def userDataAccess: UserDataAccess[T]

  def constraints: Seq[Constraint[T]]

  def requiredChangesIn(item: T): List[Change] = {
    val featuresToRemove = ManagedAttributes.get(item) match {
      case Some(features) =>
        features.filterNot { checked =>
          constraints.exists(_.feature == checked)
        }
      case _ =>
        Nil
    }

    val featureUpdates = constraints.flatMap(_.apply(item))

    if (featureUpdates.nonEmpty || featuresToRemove.nonEmpty)
      List(Mutation[T](item, featureUpdates, featuresToRemove))
    else
      Nil
  }
}

abstract class Spec[FXType <: Node] extends ConstraintBasedReconcilation[FXType] {

  implicit def specifiedClass: Class[FXType]
  def materialize(): FXType

  override def reconcilationSteps(otherItem: Node): Option[List[Change]] = {
    otherItem match {
      case expected: FXType @unchecked if specifiedClass == expected.getClass =>
        Some(requiredChangesIn(expected))
      case _ =>
        None
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
