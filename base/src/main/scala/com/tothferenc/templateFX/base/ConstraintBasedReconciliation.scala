package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.ManagedAttributes
import com.tothferenc.templateFX.Mutation

abstract class ConstraintBasedReconciliation[T] extends Template[T] {

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

    val featureUpdates = for {
      constraint <- constraints if constraint.maintained
      update <- constraint.apply(item)
    } yield update

    if (featureUpdates.nonEmpty || featuresToRemove.nonEmpty)
      List(Mutation[T](item, featureUpdates, featuresToRemove))
    else
      Nil
  }
}
