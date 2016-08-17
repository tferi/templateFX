templateFX
==========

[![Gitter](https://badges.gitter.im/gitterHQ/gitter.svg)](https://gitter.im/tferi/templateFX)

TemplateFX is a JavaFX UI definition and reconciliation library, written in Scala. It brings React.js-like functionality to the JVM.

The library offers a declarative, typesafe interface for describing UI fragments.
It's a typesafe FXML alternative which allows its users to define templates as the function of some input.
The template language is easy to use, and it allows users to effectively decouple UI-specific code from the business logic of the application.
Users also gain the ability to regenerate the templates based on different input, and reconcile the result with the scene graph.
This means that the library will ensure that the state of the UI conforms to the specification in the template.

To see how it works in practice, see the [example application](examples/src/main/scala/com/tothferenc/templateFX/examples/todo), or just check out its [view definition](examples/src/main/scala/com/tothferenc/templateFX/examples/todo/view/TodoView.scala)!

To define a vertical list of Strings or a placeholder if the list is empty, we could write:
```
def vlist(items: List[String]): Template[Node] = items match {
    case Nil =>
      node[Label](text ~ "The list is empty.", styleClasses ~ List("list-placeholder"))
    case nonEmpty => node[VBox](
      children ~~ nonEmpty.map { str =>
        node[Label](text ~ str)
      }
    )
  }
```
The output of this function is a lightweight template which may be used to either instantiate the defined objects, or to change the properties of existing ones until the desired state is reached.

These templates may be reconstructed and applied to the scene graph after every change in the application model. This way, the application View is kept in sync with the Model without low-level property bindings and procedural updates, making development simpler and safer.

Try it!
-------

To try the current snapshot, add the following to your build.sbt:
```
resolvers ++= Seq(
	"Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
libraryDependencies ++= Seq(
	"com.tothferenc" % "templatefx-base_2.11" % "0.1-SNAPSHOT",
	"com.tothferenc" % "templatefx-javafx_2.11" % "0.1-SNAPSHOT"
)
```

Attributes
----------
This library reconciles object graphs in a typesafe manner, so it needs to know about the properties of the objects the user wants it to generate/synchronize.
For each type `T`, the user may define any number of `Attribute[T,U]` values which represent ways in which the object may change (the text of a Label, the list of Tabs in a TabPane, etc).
After they are defined, these `Attribute[T,U]` instances may be used in `Template[T]` specifications, and they may be bound to a value of type `U`.
Templates constructed this way may be diffed to objects of type `T`, resulting in the list of `Change` objects necessary to make all of the template's `Attribute`s conform to the `Template`.

The library offers an ever-growing list of JavaFX attributes (although not all - pull requests are welcome!), along with convenience macros for helping users define their own.
For instance, a `Label`'s text may be defined as follows:
```
val text = Attribute.simple[Labeled, String]("Text", null)
```

In order to work as expected, the library needs to be able to check deep equality between instances of the `U` type in `Attribute[T,U]`.
For those cases when changing the equals method of the class is not possible/desired, the library offers a way to supply a deep equality method like this:
```
private val dataEquality: ((PieChart.Data, PieChart.Data)) => Boolean =
  { case (d1, d2) => d1.getName == d2.getName && d1.getPieValue == d2.getPieValue }

val data: Attribute[PieChart, List[Data]] = Attribute.listCustomEquals[PieChart, PieChart.Data]("Data", dataEquality)
```

Project structure
-----------------
The project consists of the following modules:

### Base
The base module is made up of classes which are concerned with template descriptions and reconciliation methods. This module is (and is meant to be kept) UI technology independent, which means that it does not depend on JavaFX classes.

### JavaFX
The JavaFX module defines attributes and feature storage methods for JavaFX classes.

### Example
The example module contains an application which renders its UI with the Base and JavaFX modules.

Coming soon!
