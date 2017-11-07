package com.tothferenc.templateFX.change

import java.util.{List => JList}

import com.tothferenc.templateFX.FeatureUpdate
import com.tothferenc.templateFX.PresentFeatures
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.base.attribute.Attribute
import com.tothferenc.templateFX.base.attribute.RemovableFeature
import com.tothferenc.templateFX.collection.SpecsWithKeys

import scala.collection.convert.decorateAsJava._
import scala.language.existentials
import scala.reflect.ClassTag

final case class RemoveNodes[Container, Item](collection: JList[Item], nodes: Iterable[Item]) extends Change {
  override protected def exec(): Unit = collection.removeAll(nodes.asJavaCollection)
}

final case class RemoveSeq[Container](collection: JList[_], fromInclusive: Int, toExclusive: Int) extends Change {
  override protected def exec(): Unit = {
    fromInclusive.until(toExclusive).foreach(collection.remove)
  }
}

final case class Insert[Container, Item](collection: JList[Item], definition: Template[Item], position: Int) extends Change {
  override protected def exec(): Unit = collection.add(position, definition.build())
}

final case class InsertWithKey[Container, Item, Key: ClassTag](collection: JList[Item], definition: Template[Item], position: Int, key: Key) extends Change {
  override protected def exec(): Unit = collection.add(position, SpecsWithKeys.setKeyOnItem(key, definition.build()))
}

final case class Replace[Container, Item](collection: JList[Item], definition: Template[Item], position: Int) extends Change {
  override protected def exec(): Unit = collection.set(position, definition.build())
}

final case class MoveNode[Container, Item](collection: JList[Item], item: Item, targetPosition: Int) extends Change {
  override protected def exec(): Unit = {
    // TODO that indexOf is bad.
    val currentPosition = collection.indexOf(item)
    if (currentPosition != targetPosition) {
      collection.remove(item)
      collection.add(targetPosition, item)
    }
  }
}

final case class Mutation[Item](item: Item, featureSetters: Iterable[FeatureUpdate[Item]], featuresToRemove: Iterable[RemovableFeature[Item]]) extends Change {
  override protected def exec(): Unit = {
    val managedAttributes = PresentFeatures.getOrInit(item)

    featuresToRemove.foreach { attribute =>
      attribute.remove(item)
      managedAttributes -= attribute
    }

    featureSetters.foreach { setter =>
      setter.executeOn(item)
      managedAttributes += setter.feature
    }
  }
}

final case class ChangeAttribute[Container, FixedItem](container: Container, fixture: Attribute[Container, FixedItem], spec: Option[Template[FixedItem]]) extends Change {
  override protected def exec(): Unit = spec match {
    case Some(template) => fixture.set(container, template.build())
    case _ => fixture.remove(container)
  }
}
