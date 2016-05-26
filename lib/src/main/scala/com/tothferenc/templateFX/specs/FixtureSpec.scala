package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag
import scala.language.existentials

final case class FixtureSpec[Container](
    constraints: Seq[Constraint[Container]],
    parameterizedFixtures: List[ParameterizedFixture[Container, _]],
    constructorParams: Any*
)(
    implicit
    classTag: ClassTag[Container],
    override val userDataAccess: UserDataAccess[Container]
) extends Fixtures[Container] {
  override implicit val specifiedClass: Class[Container] = implicitly[ClassTag[Container]].runtimeClass.asInstanceOf[Class[Container]]
}
