package com.tothferenc.templateFX

import com.tothferenc.templateFX.attribute.RemovableFeature
import com.tothferenc.templateFX.specs.Fixture
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scala.collection.convert.decorateAsJava._

object Change {
  lazy val logger = Logger(LoggerFactory.getLogger("CHANGELOG"))

  val debug: Boolean = java.lang.Boolean.getBoolean("tfx-debug")
}

sealed abstract class Change extends Product with Serializable {

  def execute(): Unit = {
    if (Change.debug) Change.logger.debug(this.toString)
    exec()
  }

  protected def exec(): Unit
}

final case class SetFixture[Container, FixedItem](container: Container, fixture: Fixture[Container, FixedItem], spec: Option[Template[FixedItem]]) extends Change {
  override protected def exec(): Unit = fixture.set(container, spec.map(_.build()).getOrElse(fixture.default))
}

final case class RemoveNode[Container, Item](container: Container, item: Item)(implicit collectionAccess: CollectionAccess[Container, Item]) extends Change {
  override protected def exec(): Unit = collectionAccess.getCollection(container).remove(item)
}

final case class RemoveNodes[Container, Item](container: Container, nodes: Seq[Item])(implicit collectionAccess: CollectionAccess[Container, Item]) extends Change {
  override protected def exec(): Unit = collectionAccess.getCollection(container).removeAll(nodes.asJavaCollection)
}

final case class RemoveSeq[Container](container: Container, fromInclusive: Int, toExclusive: Int)(implicit collectionAccess: CollectionAccess[Container, _]) extends Change {
  override protected def exec(): Unit =  {
    val remove = collectionAccess.getCollection(container).remove _
    fromInclusive.until(toExclusive).foreach(remove)
  }
}

final case class Insert[Container, Item](container: Container, definition: Template[Item], position: Int)(implicit collectionAccess: CollectionAccess[Container, Item]) extends Change {
  override protected def exec(): Unit = collectionAccess.getCollection(container).add(position, definition.build())
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
