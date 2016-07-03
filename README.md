templateFX
==========

[![Build Status](https://travis-ci.org/tferi/templateFX.svg?branch=master)](https://travis-ci.org/tferi/templateFX)
[![Gitter](https://badges.gitter.im/gitterHQ/gitter.svg)](https://gitter.im/tferi/templateFX)

TemplateFX is a JavaFX UI definition and reconciliation library, written in Scala. It brings React.js-like functionality to the JVM.

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

TemplateFX allows its users to define arbitrary view templates with its API. These templates may be reconciled with another object graph, meaning that the library will make changes to the target until it conforms to the template.

This capability allows the library's users to describe the desired state of the UI in a declarative, typesafe manner. The template language is easy to use, and it allows users to effectively decouple UI-specific code from the business logic of the application.

To see how it works in practice, see the [example application](examples/src/main/scala/com/tothferenc/templateFX/examples/todo), or check out its [view definition](examples/src/main/scala/com/tothferenc/templateFX/examples/todo/view/TodoView.scala)!

Project structure
-----------------
The project consists of the following modules:

### Base
The base module is made up of classes which are concerned with template descriptions and reconcilation methods. This module is (and is meant to be kept) UI technology independent, which means that it does not depend on JavaFX classes.

### JavaFX
The JavaFX module defines attributes and feature storage methods for JavaFX classes.

### Example
The example module contains an application which renders its UI with the Base and JavaFX modules.

Coming soon!
