package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.specs.base.ReflectiveSpec
import com.tothferenc.templateFX.userdata._

import scala.reflect._

final case class Hierarchy[SubParent <: TFXParent](
    constraints: Seq[Constraint[SubParent]],
    children: CollectionSpec[TFXParent, Node]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[SubParent]) extends ReflectiveSpec[SubParent] with NodeDataAccess[SubParent] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[SubParent]]

  override def initNodesBelow(instance: SubParent): Unit = instance.getChildren.addAll(children.materializeAll(): _*)

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other).map {
      _ ::: children.requiredChangesIn(other.asInstanceOf[SubParent])
    }
  }

}


final case class Leaf[FXType <: Node](
                                       constraints: Seq[Constraint[FXType]]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] with NodeDataAccess[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override def initNodesBelow(instance: FXType): Unit = ()

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other)
  }

}

