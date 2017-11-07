package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.PresentFeatures
import com.tothferenc.templateFX.change.Change
import com.tothferenc.templateFX.change.Mutation

abstract class ConstraintBasedReconciliation[T] extends Template[T] {

  def constraintsToApply: Seq[Constraint[T]]

  def requiredChangesIn(item: T): Iterable[Change] = {
    val featuresToRemove = PresentFeatures.get(item) match {
      case Some(presentFeatures) =>
        presentFeatures.filterNot { presentFeature =>
          constraintsToApply.exists(_.feature == presentFeature)
        }
      case _ =>
        Nil
    }

    val featureUpdates = for {
      constraint <- constraintsToApply if constraint.maintained
      update <- constraint.apply(item)
    } yield update

    if (featureUpdates.nonEmpty || featuresToRemove.nonEmpty)
      List(Mutation[T](item, featureUpdates, featuresToRemove))
    else
      Nil
  }
}
