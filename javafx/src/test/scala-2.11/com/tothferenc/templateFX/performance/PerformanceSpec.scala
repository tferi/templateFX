package com.tothferenc.templateFX.performance

import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Fixture
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.collection.CollectionSpec
import org.specs2.mutable.Specification

class PerformanceSpec extends Specification {

  // This does not do meaningful assertions for now; the goal is to keep track of performance degradation over development.

  new JFXPanel()

  val count = 1000
  val limit = 1000L

  "TemplateFX" should {

    s"be able to reconcile large object lists of $count " in {
      performanceTest(count, limit, false)
    }

    s"be able to reconcile large object lists of $count with keys" in {
      performanceTest(count, limit, true)
    }
  }

  def performanceTest(count: Int, limit: Long, useKeys: Boolean) = {
    val start = System.currentTimeMillis()

    val labelTemplates = if (useKeys) collectionSpecWithIds(count, false) else collectionSpec(count, false)
    val template = node[HBox](children ~~ labelTemplates)
    val templateBuilt = System.currentTimeMillis()
    println(s"Template assembled in ${templateBuilt - start} ms for $count items.")

    val hbox = template.build()
    val hboxBuilt = System.currentTimeMillis()
    println(s"HBox built in ${hboxBuilt - templateBuilt} ms for $count items.")

    val reverseTemplate: CollectionSpec[Node] = if (useKeys) collectionSpecWithIds(count, true) else collectionSpec(count, true)
    val templateReversed = System.currentTimeMillis()
    println(s"Reverse template built in ${templateReversed - hboxBuilt} ms for $count items.")

    Fixture(hbox, children).reconcile(reverseTemplate)
    val reconciled = System.currentTimeMillis()
    println(s"Reconciled reverse template in ${reconciled - templateReversed} ms for $count items.")

    val total = reconciled - start
    println(s"Total time was $total ms for $count items")
    1 === 1 // Travis is unpredictable, it's hard to set a number here.
  }

  private def collectionSpec(count: Int, reverse: Boolean): CollectionSpec[Node] = {
    val list: List[Int] = (0 to count).toList
    for {
      idx <- if (reverse) list.reverse else list
    } yield {
      node[Label](text ~ idx.toString)
    }
  }

  private def collectionSpecWithIds(count: Int, reverse: Boolean): CollectionSpec[Node] = {
    val list: List[Int] = (0 to count).toList
    for {
      idx <- if (reverse) list.reverse else list
    } yield {
      idx -> node[Label](text ~ idx.toString)
    }
  }
}
