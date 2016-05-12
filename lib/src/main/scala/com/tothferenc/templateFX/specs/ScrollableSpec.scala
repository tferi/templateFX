package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.ScrollPane

import com.tothferenc.templateFX._
import scala.language.existentials
import scala.reflect.ClassTag

case object ScrollableContent extends NodeFixture[ScrollPane] {
  override def get(container: ScrollPane): Option[Node] = Option(container.getContent)
  override def set(container: ScrollPane, node: Node): Unit = container.setContent(node)
}

final case class ScrollableSpec[Scrollable <: ScrollPane, Content <: Node](
    constraints: Seq[Constraint[Scrollable]],
    contentSpec: Spec[Content]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[Scrollable]) extends ReflectiveSpec[Scrollable] {

  override implicit val specifiedClass: Class[Scrollable] = classTag.runtimeClass.asInstanceOf[Class[Scrollable]]

  override def children: ChildrenSpec = Ignore

  override def initNodesBelow(instance: Scrollable): Unit = instance.setContent(contentSpec.materialize())

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == specifiedClass) {
      val scrollable: Scrollable = node.asInstanceOf[Scrollable]
      calculateMutation(scrollable) ::: ScrollableContent.reconcile(scrollable, Some(contentSpec))
    } else {
      List(Replace(container, this, position))
    }
  }
}
