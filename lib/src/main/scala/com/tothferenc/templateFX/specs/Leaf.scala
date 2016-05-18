package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX.Change
import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.specs.base.ReflectiveSpec

import scala.reflect.ClassTag

final case class Leaf[FXType <: Node](
    constraints: Seq[Constraint[FXType]]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] with NodeDataAccess[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override def initNodesBelow(instance: FXType): Unit = ()

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other)
  }

}
