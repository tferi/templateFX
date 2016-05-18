package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.userdata.ManagedAttributes
import com.tothferenc.templateFX.userdata.UserDataAccess

abstract class Template[+T] {
  def build(): T
  def reconcilationSteps(other: Any): Option[List[Change]]
}

abstract class ConstraintBasedReconcilation[T] extends Template[T] {

  implicit protected def userDataAccess: UserDataAccess[T]

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

abstract class Spec[T] extends ConstraintBasedReconcilation[T] {

  implicit def specifiedClass: Class[T]

  override def reconcilationSteps(otherItem: Any): Option[List[Change]] = {
    otherItem match {
      case expected: T @unchecked if specifiedClass == expected.getClass =>
        Some(requiredChangesIn(expected))
      case _ =>
        None
    }
  }
}

abstract class ReflectiveSpec[T] extends Spec[T] {

  protected def constructorParams: Seq[Any]

  def initNodesBelow(instance: T): Unit

  def build(): T = {
    val instance = UniversalConstructor.instantiate[T](constructorParams)
    Mutation(instance, constraints.flatMap(_(instance)), Nil).execute()
    initNodesBelow(instance)
    instance
  }
}
