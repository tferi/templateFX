package com.tothferenc.templateFX

import java.util

import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.base.TemplateNode
import org.junit.rules.TestName
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

import scala.collection.convert.wrapAsScala._

package object generators {

  val alphaStrGen = Gen.alphaStr.map(_.take(5))

  implicit val arbitraryAlphaStr = Arbitrary(alphaStrGen)

  val strAttributeGen: Gen[StringAttribute] = {
    for {
      name <- arbitrary[String]
    } yield {
      StringAttribute(name)
    }
  }

  val attributeSetGen: Gen[Set[StringAttribute]] = {
    for {
      length <- Gen.choose(2, 5)
      set <- Gen.containerOfN[Set, StringAttribute](length, strAttributeGen)
    } yield {
      set
    }
  }

  implicit val arbitraryAttrSet = Arbitrary(attributeSetGen)

  val nodeGen: Gen[TestNode] = {
    for {
      attributes <- arbitrary[Set[StringAttribute]]
    } yield {
      new TestNode(attributes)
    }
  }

  implicit val arbitraryNode = Arbitrary(nodeGen)

  def templateGen(attributes: Set[StringAttribute]): Gen[TemplateNode[TestNode]] = {
    for {
      values <- valuesForAttributes(attributes)
    } yield {
      TemplateNode[TestNode](values.map {
        case (attribute, value) => SimpleBinding(attribute, value, maintained = true)
      }, List(attributes))
    }
  }

  private def valuesForAttributes(attributes: Set[StringAttribute]): Gen[Seq[(StringAttribute, String)]] = {
    Gen.sequence {
      attributes.map { attribute =>
        arbitrary[String].map { value =>
          attribute -> value
        }
      }
    }.map(_.toSeq)
  }
}
