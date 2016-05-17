package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.ScrollPane

import com.tothferenc.templateFX._
import scala.language.existentials
import scala.reflect.ClassTag

case object ScrollableContent extends NodeFixture[ScrollPane] {
  override def read(container: ScrollPane): Option[Node] = Option(container.getContent)
  override def set(container: ScrollPane, node: Node): Unit = container.setContent(node)
}

object ScrollSpec {
  val fixtures = List(ScrollableContent)
}

final case class ScrollSpec[Scrollable <: ScrollPane](
    constraints: Seq[Constraint[Scrollable]],
    contentSpec: NodeSpec
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[Scrollable]) extends Fixtures[Scrollable] {

  override implicit val specifiedClass: Class[Scrollable] = classTag.runtimeClass.asInstanceOf[Class[Scrollable]]

  override def children: ChildrenSpec = Ignore

  override def fixtures: List[NodeFixture[Scrollable]] = ScrollSpec.fixtures

  override val specs: List[Option[NodeSpec]] = List(Some(contentSpec))
}
