package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag

final case class FixtureSpec[Container, FixedItem](
    constraints: Seq[Constraint[Container]],
    contentTemplate: Template[FixedItem], constructorParams: Any*
)(
    implicit
    classTag: ClassTag[Container],
    override val userDataAccess: UserDataAccess[Container],
    nodeFixture: Fixture[Container, FixedItem]
) extends Fixtures[Container, FixedItem] {
  override implicit val specifiedClass: Class[Container] = implicitly[ClassTag[Container]].runtimeClass.asInstanceOf[Class[Container]]

  override val fixtures: List[Fixture[Container, FixedItem]] = List(nodeFixture)

  override def specs: List[Option[Template[FixedItem]]] = List(Some(contentTemplate))
}
