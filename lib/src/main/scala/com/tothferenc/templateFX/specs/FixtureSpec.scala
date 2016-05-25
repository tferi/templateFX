package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag

final case class FixtureSpec[Container](
    constraints: Seq[Constraint[Container]],
    contentTemplate: Template[Node], constructorParams: Any*
)(
    implicit
    classTag: ClassTag[Container],
    override val userDataAccess: UserDataAccess[Container],
    nodeFixture: NodeFixture[Container]
) extends Fixtures[Container] {
  override implicit val specifiedClass: Class[Container] = implicitly[ClassTag[Container]].runtimeClass.asInstanceOf[Class[Container]]

  override val fixtures: List[NodeFixture[Container]] = List(implicitly[NodeFixture[Container]])

  override def specs: List[Option[Template[Node]]] = List(Some(contentTemplate))
}
