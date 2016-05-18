package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.Tab

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata._

import scala.reflect.ClassTag

object TabSpec {

  case object TabContent extends NodeFixture[Tab] {
    override def read(container: Tab): Option[Node] = Option(container.getContent)
    override def set(container: Tab, node: Node): Unit = container.setContent(node)
  }

  private val fixtures = List(TabContent)
}

final case class TabSpec[SubTab <: Tab](constraints: Seq[Constraint[SubTab]], contentTemplate: Template[Node], constructorParams: Any*)(implicit classTag: ClassTag[SubTab]) extends Fixtures[SubTab] {
  override implicit val specifiedClass: Class[SubTab] = classTag.runtimeClass.asInstanceOf[Class[SubTab]]

  override implicit def userDataAccess: UserDataAccess[SubTab] = tabUserDataAccess

  override val fixtures: List[NodeFixture[SubTab]] = TabSpec.fixtures

  override def specs: List[Option[Template[Node]]] = List(Some(contentTemplate))
}
