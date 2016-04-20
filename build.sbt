import sbt.Keys._

val commonSettings = Seq(
	version := "1.0",

	scalaVersion := "2.11.8",

	libraryDependencies ++= Seq(
		"ch.qos.logback" %  "logback-classic" % "1.1.7", // Needed by scala-logging
		"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
		"org.specs2" %% "specs2-core" % "3.7.2" % "test"),

	scalacOptions in Test ++= Seq("-Yrangepos")
)

val lib = (project in file("lib"))
  .settings(commonSettings)

val examples = (project in file("examples"))
  .settings(commonSettings)
	.dependsOn(lib)

val root = (project in file("."))
  .dependsOn(lib, examples)
	.aggregate(lib, examples)
	.settings(commonSettings)
	.settings(Seq(
		name := "templateFx"
	))