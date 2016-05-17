package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.userdata.ManagedAttributes
import com.tothferenc.templateFX.userdata.UserDataAccess

abstract class Template {
  type Output
  def materialize(): Output
  def reconcilationSteps(other: Node): Option[List[Change]]
}

object Template {
  type Aux[Out] = Template { type Output = Out }
}

abstract class ConstraintBasedReconcilation extends Template {

  implicit def userDataAccess: UserDataAccess[Output]

  def constraints: Seq[Constraint[Output]]

  def requiredChangesIn(item: Output): List[Change] = {
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
      List(Mutation[Output](item, featureUpdates, featuresToRemove))
    else
      Nil
  }
}

abstract class Spec[FXType <: Node] extends ConstraintBasedReconcilation {

  type Output = FXType

  implicit def specifiedClass: Class[FXType]
  def materialize(): FXType

  override def reconcilationSteps(otherItem: Node): Option[List[Change]] = {
    otherItem match {
      case expected: Output @unchecked if specifiedClass == expected.getClass =>
        Some(requiredChangesIn(expected))
      case _ => None
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
