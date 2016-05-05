package com.tothferenc.templateFX.macros

import com.tothferenc.templateFX.attribute.Attribute

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context


object Expander {

  def m[Attr, Value](v: Value): Attribute[Attr, Value] = macro mImpl[Attr, Value]

  def mImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(v: c.Expr[Value]): c.Expr[Attribute[Attr, Value]] = {
    import c.universe._
    val attrType = weakTypeTag[Attr].tpe.typeConstructor
    val valType = weakTypeTag[Value].tpe.typeConstructor
    val expr =
			q"""new Attribute[$attrType, $valType]{
					override def read(src: $attrType): $valType = $v
          override def unset(target: $attrType): Unit = ()
          override def set(target: $attrType, value: $valType): Unit = ()
				 }"""
    c.Expr[Attribute[Attr, Value]](expr)
  }
//
//  def expand_impl(c: Context)(annottees: c.Expr[Any]*) = {
//    import c.universe._
//
//    //    def gen(objectName: Name, attrName: Name, fxType: Tree, attrType: Tree) = {
//    //        q"""
//    //          case object $objectName extends Attribute[$fxType, $attrType] {
//    //           override def readFrom(src: $fxType): $attrType = src.get$attrName
//    //           override def set(target: $fxType, value: $attrType): Unit = target.set$attrName(value)
//    //          }
//    //        """
//    //    }
//
//    annottees.map(_.tree).toList match {
//      case q"Attr[$fxType,$attrType]($objectName, $attrName)" :: Nil =>
//        val getter: String = "get" + showCode(attrName)
//        val setter: String = "set" + showCode(attrName)
//
//        val fxt: String = showCode(fxType)
//        val attrt: String = showCode(attrType)
//        val on: String = showCode(objectName)
//
//
//        c.Expr[Any](
//        q"""
//          case object $on extends Attribute[$fxt, $attrt] {
//           override def readFrom(src: $fxt): $attrt = src.$getter
//           override def set(target: $fxt, value: $attrt): Unit = target.$setter(value)
//          }
//        """
//      )
//      // Add validation and error handling here.
//    }
//  }
}