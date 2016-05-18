package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.specs.base.ReflectiveSpec
import com.tothferenc.templateFX.userdata._

import scala.reflect._

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: CollectionSpec[TFXParent, Node]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] with NodeDataAccess[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override def initNodesBelow(instance: FXType): Unit = instance match {
    case container: TFXParent =>
      container.getChildren.addAll(children.materializeAll(): _*)
    case _ =>
      ()
  }

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other).map {
      _ ::: (other match {
        case container: TFXParent =>
          children.requiredChangesIn(container)
        case leaf =>
          Nil
      })
    }
  }

}

