
import sbt.Keys._
import sbt._

val commonSettings = Seq(
	version := "1.0",

	scalaVersion := "2.11.8",

	organization := "com.tothferenc",

	version := "0.1-SNAPSHOT",

	libraryDependencies ++= Seq(
		"ch.qos.logback" %  "logback-classic" % "1.1.7",
		"org.specs2" %% "specs2-core" % "3.7.2" % "test"),

	scalacOptions in Test ++= Seq(
		"-Yrangepos"
	),

	crossScalaVersions := Seq("2.10.6, 2.11.8"),

	scalacOptions ++= Seq(
		"-Xfatal-warnings",
		"-feature"
		//"-Ymacro-debug-lite"
	),

	publishTo := {
		val nexus = "https://oss.sonatype.org/"
		if (isSnapshot.value)
			Some("snapshots" at nexus + "content/repositories/snapshots")
		else
			Some("releases"  at nexus + "service/local/staging/deploy/maven2")
	},

	publishArtifact in Test := false,

	licenses := Seq("GNU GPL v3" -> url("https://www.gnu.org/licenses/gpl-3.0.html")),

	homepage := Some(url("https://github.com/tferi/templateFX")),

	addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

val base = (project in file("base"))
  .settings(commonSettings)
  .settings(
	  name := "templatefx-base",
	  description := "Core business logic for template definition and reconciliation.",
	  publishMavenStyle := true,
	  libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
  )

val javafx = (project in file("javafx"))
	.dependsOn(base)
  .settings(commonSettings)
	.settings(Seq(
		name := "templatefx-javafx",
		description := "JavaFX-specific attributes.",
		publishMavenStyle := true
	))

val examples = (project in file("examples"))
	.dependsOn(javafx)
	.settings(commonSettings)
	.settings(Seq(
		name := "templatefx-examples",
		description := "Example application.",
		publishMavenStyle := true
	))

val root = (project in file("."))
  .dependsOn(base, javafx, examples)
	.aggregate(base, javafx, examples)
	.settings(commonSettings)
	.settings(Seq(
		name := "templatefx",
		description := "Aggregate module."
	))