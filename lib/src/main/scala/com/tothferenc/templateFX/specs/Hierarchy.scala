package com.tothferenc.templateFX.specs

import java.lang.reflect.Constructor
import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }
import com.tothferenc.templateFX.{ Change, Replace, UserData, _ }

import scala.collection.mutable.ListBuffer
import scala.reflect._

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: ChildrenSpec
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == specifiedClass) {
      calculateMutation(node.asInstanceOf[FXType])
    } else {
      List(Replace(container, this, position))
    }
  }
}

