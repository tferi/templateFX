//package com.tothferenc.templateFX.specs
//
//import javafx.scene.Node
//import javafx.scene.layout.BorderPane
//
//import com.tothferenc.templateFX._
//
//import scala.language.existentials
//import scala.reflect.ClassTag
//
//case object Top extends NodeFixture[BorderPane] {
//  override def read(container: BorderPane): Option[Node] = Option(container.getTop)
//  override def set(container: BorderPane, node: Node): Unit = container.setTop(node)
//}
//
//case object Right extends NodeFixture[BorderPane] {
//  override def read(container: BorderPane): Option[Node] = Option(container.getRight)
//  override def set(container: BorderPane, node: Node): Unit = container.setRight(node)
//}
//
//case object Bottom extends NodeFixture[BorderPane] {
//  override def read(container: BorderPane): Option[Node] = Option(container.getBottom)
//  override def set(container: BorderPane, node: Node): Unit = container.setBottom(node)
//}
//
//case object Left extends NodeFixture[BorderPane] {
//  override def read(container: BorderPane): Option[Node] = Option(container.getLeft)
//  override def set(container: BorderPane, node: Node): Unit = container.setLeft(node)
//}
//
//case object Center extends NodeFixture[BorderPane] {
//  override def read(container: BorderPane): Option[Node] = Option(container.getCenter)
//  override def set(container: BorderPane, node: Node): Unit = container.setCenter(node)
//}
//
//object BorderSpec {
//  val fixtures = List(Top, Right, Bottom, Left, Center)
//}
//
//final case class BorderSpec[Bordered <: BorderPane](
//    constraints: Seq[Constraint[Bordered]],
//    top: Option[NodeSpec],
//    right: Option[NodeSpec],
//    bottom: Option[NodeSpec],
//    left: Option[NodeSpec],
//    center: Option[NodeSpec]
//)(protected val constructorParams: Any*)(implicit classTag: ClassTag[Bordered]) extends Fixtures[Bordered] {
//
//  override implicit val specifiedClass: Class[Bordered] = classTag.runtimeClass.asInstanceOf[Class[Bordered]]
//
//  override def children: ChildrenSpec = Ignore
//
//  override def fixtures: List[NodeFixture[Bordered]] = BorderSpec.fixtures
//
//  override val specs: List[Option[NodeSpec]] = List(top, right, bottom, left, center)
//}
