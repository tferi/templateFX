import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._
import sbt._

import scala.reflect.ClassTag
import scalariform.formatter.preferences.DanglingCloseParenthesis
import scalariform.formatter.preferences.Preserve

val commonSettings = Seq(
	version := "1.0",

	scalaVersion := "2.11.7",

	ScalariformKeys.preferences := ScalariformKeys.preferences.value
		  .setPreference(DanglingCloseParenthesis, Preserve),

	libraryDependencies ++= Seq(
		"ch.qos.logback" %  "logback-classic" % "1.1.7", // Needed by scala-logging
		"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
		"org.specs2" %% "specs2-core" % "3.7.2" % "test"),

	scalacOptions in Test ++= Seq(
		"-Yrangepos"
	),

	scalacOptions ++= Seq(
		"-Xfatal-warnings",
		"-feature"
		//"-Ymacro-debug-lite"
	),

	addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

val macros = (project in file("macros"))
  .settings(commonSettings)
  .settings(
	  libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
  )

val lib = (project in file("lib"))
	.dependsOn(macros)
  .settings(commonSettings)

val examples = (project in file("examples"))
  .settings(commonSettings)
	.dependsOn(lib)

val root = (project in file("."))
  .dependsOn(macros, lib, examples)
	.aggregate(macros, lib, examples)
	.settings(commonSettings)
	.settings(Seq(
		name := "templateFx"
	))