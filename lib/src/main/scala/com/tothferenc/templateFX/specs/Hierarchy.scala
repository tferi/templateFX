package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.userdata._

import scala.reflect._

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: CollectionSpec[TFXParent, Node]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override implicit def userDataAccess: UserDataAccess[FXType] = nodeUserDataAccess

  override def initNodesBelow(instance: FXType): Unit = instance match {
    case container: TFXParent =>
      container.getChildren.addAll(children.materializeAll(): _*)
    case _ =>
      ()
  }

  override def reconcilationSteps(otherItem: Any): Option[List[Change]] = {
    super.reconcilationSteps(otherItem).map {
      _ ::: (otherItem match {
        case container: TFXParent =>
          children.requiredChangesIn(container)
        case leaf =>
          Nil
      })
    }
  }

}

