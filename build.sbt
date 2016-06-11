
import sbt.Keys._
import sbt._

val commonSettings = Seq(
	version := "1.0",

	scalaVersion := "2.11.7",

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

val base = (project in file("base"))
  .settings(commonSettings)
  .settings(
	  libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
  )

val javafx = (project in file("javafx"))
	.dependsOn(base)
  .settings(commonSettings)

val examples = (project in file("examples"))
  .settings(commonSettings)
	.dependsOn(javafx)

val root = (project in file("."))
  .dependsOn(base, javafx, examples)
	.aggregate(base, javafx, examples)
	.settings(commonSettings)
	.settings(Seq(
		name := "templateFx"
	))