package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag

trait NodeDataAccess[T <: Node] {
  implicit protected def userDataAccess: UserDataAccess[T] = nodeUserDataAccess
}

final case class TabPaneSpec[SubTabPane <: TabPane](
  constraints: Seq[Constraint[SubTabPane]],
    tabTemplates: CollectionSpec[SubTabPane, Tab],
    constructorParams: Any*
)(implicit classTag: ClassTag[SubTabPane]) extends ReflectiveSpec[SubTabPane] with NodeDataAccess[SubTabPane] {

  override implicit val specifiedClass: Class[SubTabPane] = classTag.runtimeClass.asInstanceOf[Class[SubTabPane]]

  override def initNodesBelow(instance: SubTabPane): Unit = instance.getTabs.addAll(tabTemplates.materializeAll(): _*)

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other).map {
      _ ::: (other match {
        case container: SubTabPane =>
          tabTemplates.requiredChangesIn(container)
        case leaf =>
          Nil
      })
    }
  }
}

