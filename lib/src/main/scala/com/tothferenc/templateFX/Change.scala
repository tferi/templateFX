package com.tothferenc.templateFX

import java.util.{ List => JList }

import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.RemovableFeature
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.userdata._

import scala.collection.convert.decorateAsJava._
import scala.language.existentials

final case class RemoveNode[Container, Item](collection: JList[Item], item: Item) extends Change {
  override protected def exec(): Unit = collection.remove(item)
}

final case class RemoveNodes[Container, Item](collection: JList[Item], nodes: Seq[Item]) extends Change {
  override protected def exec(): Unit = collection.removeAll(nodes.asJavaCollection)
}

final case class RemoveSeq[Container](collection: JList[_], fromInclusive: Int, toExclusive: Int) extends Change {
  override protected def exec(): Unit = {
    val remove = collection.remove _
    fromInclusive.until(toExclusive).foreach(remove)
  }
}

final case class Insert[Container, Item](collection: JList[Item], definition: Template[Item], position: Int) extends Change {
  override protected def exec(): Unit = collection.add(position, definition.build())
}

final case class InsertWithKey[Container, Item, Key](container: Container, definition: Template[Item], position: Int, key: Key)(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends Change {
  override protected def exec(): Unit = collectionAccess.getCollection(container).add(position, SpecsWithKeys.setKeyOnItem(key, definition.build()))
}

final case class Replace[Container, Item](container: Container, definition: Template[Item], position: Int)(implicit collectionAccess: CollectionAccess[Container, Item]) extends Change {
  override protected def exec(): Unit = collectionAccess.getCollection(container).set(position, definition.build())
}

final case class MoveNode[Container, Item](container: Container, item: Item, targetPosition: Int)(implicit collectionAccess: CollectionAccess[Container, Item]) extends Change {
  override protected def exec(): Unit = {
    val collection = collectionAccess.getCollection(container)
    val currentPosition = collection.indexOf(item)
    if (currentPosition != targetPosition) {
      collection.remove(item)
      collection.add(targetPosition, item)
    }
  }
}

final case class Mutation[Item: UserDataAccess](item: Item, featureSetters: Seq[FeatureSetter[Item]], featuresToRemove: Iterable[RemovableFeature[Item]]) extends Change {
  override protected def exec(): Unit = {
    val managedAttributes = ManagedAttributes.getOrInit(item)

    featuresToRemove.foreach { attribute =>
      attribute.remove(item)
      managedAttributes -= attribute
    }

    featureSetters.foreach { setter =>
      setter.set(item)
      managedAttributes += setter.feature
    }
  }
}
