name := "chisel-packaging"

organization := "esa.cs.tu-darmstadt.de"

version := "0.2-SNAPSHOT"

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.10.3", "2.10.4", "2.11.0")

libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel" % "latest.release",
  "com.typesafe.play" %% "play-json" % "2.4.8"
)

