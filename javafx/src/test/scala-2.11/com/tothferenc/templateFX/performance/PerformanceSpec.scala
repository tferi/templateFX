package com.tothferenc.templateFX.performance

import javafx.embed.swing.JFXPanel
import javafx.scene.control.Label
import javafx.scene.layout.HBox

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Fixture
import com.tothferenc.templateFX.base.Template
import org.specs2.mutable.Specification

class PerformanceSpec extends Specification {

  // This does not do meaningful assertions for now; the goal is to keep track of performance degradation over development.

  new JFXPanel()

  "TemplateFX" should {

    val count = 1000

    s"be able to reconcile large object lists of $count under a 200 ms" in {
      val start = System.currentTimeMillis()

      val labelTemplates: List[Template[Label]] = for {
        idx <- (0 to count).toList
      } yield {
        node[Label](text ~ idx.toString)
      }
      val template = node[HBox](children ~~ labelTemplates)
      val templateBuilt = System.currentTimeMillis()
      println(s"Template assembled in ${templateBuilt - start} ms for $count items.")

      val hbox = template.build()
      val hboxBuilt = System.currentTimeMillis()
      println(s"HBox built in ${hboxBuilt - templateBuilt} ms for $count items.")

      val templateReversed = System.currentTimeMillis()
      println(s"Reverse template built in ${templateReversed - hboxBuilt} ms for $count items.")

      Fixture(hbox, children).reconcile(labelTemplates.reverse)
      val reconciled = System.currentTimeMillis()
      println(s"Reconciled reverse template in ${reconciled - templateReversed} ms for $count items.")

      val total = reconciled - start
      println(s"Total time was $total ms for $count items")
      total must beLessThan(200L)
    }
  }
}
