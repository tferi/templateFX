package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag

trait NodeDataAccess[T <: Node] {
  implicit protected def userDataAccess: UserDataAccess[T] = nodeUserDataAccess
}

final case class TabPaneSpec[SubTabPane <: TabPane](
  constraints: Seq[Constraint[SubTabPane]],
    tabTemplates: Template[Tab],
    constructorParams: Any*
)(implicit classTag: ClassTag[SubTabPane]) extends Fixtures[SubTabPane] with NodeDataAccess[SubTabPane] {

  override def fixtures: List[NodeFixture[SubTabPane]] = ???

  override implicit val specifiedClass: Class[SubTabPane] = classTag.runtimeClass.asInstanceOf[Class[SubTabPane]]

  override def specs: List[Option[Template[Node]]] = ???
}

