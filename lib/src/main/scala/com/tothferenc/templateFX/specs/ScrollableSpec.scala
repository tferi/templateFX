package com.tothferenc.templateFX.specs

import javafx.scene.Node
import javafx.scene.control.ScrollPane

import com.tothferenc.templateFX._
import scala.language.existentials
import scala.reflect.ClassTag

final case class ScrollableSpec[Scrollable <: ScrollPane, Content <: Node](
    constraints: Seq[Constraint[Scrollable]],
    contentSpec: Spec[Content]
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[Scrollable]) extends ReflectiveSpec[Scrollable] {

  override implicit val specifiedClass: Class[Scrollable] = classTag.runtimeClass.asInstanceOf[Class[Scrollable]]

  override def children: ChildrenSpec = Ignore

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == specifiedClass) {
      val scrollable: Scrollable = node.asInstanceOf[Scrollable]
      calculateMutation(scrollable) ::: {
        Option(scrollable.getContent) match {

          case Some(existing) if existing.getClass == contentSpec.specifiedClass =>
            contentSpec.calculateMutation(existing.asInstanceOf[Content])

          case _ =>
            scrollable.setContent(contentSpec.materialize())
            Nil
        }
      }
    } else {
      List(Replace(container, this, position))
    }
  }
}
