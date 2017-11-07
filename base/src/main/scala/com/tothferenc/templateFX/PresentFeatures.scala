package com.tothferenc.templateFX

import com.tothferenc.templateFX.base.attribute.RemovableFeature

import scala.collection.mutable

object PresentFeatures {

  private type Features[Item] = mutable.Set[RemovableFeature[Item]]

  private type FeaturesImpl[Item] = mutable.HashSet[RemovableFeature[Item]]

  private val featuresByItem: mutable.WeakHashMap[Any, Any] = mutable.WeakHashMap.empty

  def getOrInit[Item](node: Item): Features[Item] = {
    featuresByItem.getOrElseUpdate(node, new FeaturesImpl[Item]).asInstanceOf[FeaturesImpl[Item]]
  }

  def get[Item](node: Item): Option[Features[Item]] = {
    featuresByItem.get(node).asInstanceOf[Option[FeaturesImpl[Item]]]
  }

  def set[Item](node: Item, attributes: Features[Item]): Unit = {
    featuresByItem += node -> attributes
  }
}
