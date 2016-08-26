package com.tothferenc.templateFX.scalacheck

import com.tothferenc.templateFX.SimpleBinding
import com.tothferenc.templateFX.StringAttribute
import com.tothferenc.templateFX.TestNode
import com.tothferenc.templateFX.base.Attribute
import com.tothferenc.templateFX.base.TemplateNode
import com.tothferenc.templateFX.generators._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class GenTemplateSpec extends Specification with ScalaCheck {

  "Nodes" should {
    "be instantiated according to the specification in the template" in {
      val attributeSet = arbitrary[Set[StringAttribute]].sample.get
      val attributeNames = attributeSet.map(_.name)
      implicit val arbitraryTemplate: Arbitrary[TemplateNode[TestNode]] = Arbitrary(templateGen(attributeSet))

      forAll { nodeTpl: TemplateNode[TestNode] =>
        val node = nodeTpl.build()
        val attributesInTemplate: Map[Attribute[TestNode, String], String] = nodeTpl.constraintsToApply.map { constraint =>
          val binding: SimpleBinding[TestNode, String] = constraint.asInstanceOf[SimpleBinding[TestNode, String]]
          binding.feature -> binding.value
        }.toMap
        attributesInTemplate mustEqual node.currentState
      }
    }
  }

}
