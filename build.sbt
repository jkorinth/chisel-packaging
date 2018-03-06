name := "chisel-packaging"

organization := "esa.cs.tu-darmstadt.de"

version := "0.4-SNAPSHOT"

scalaVersion := "2.12.4"

unmanagedResources in Compile ++= Seq(
  baseDirectory.value / "package.py",
  baseDirectory.value / "axi4.py"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
val defaultVersions = Map("chisel3"          -> "3.0.2",
                          "chisel-iotesters" -> "1.1.2")

libraryDependencies ++= (Seq("chisel3","chisel-iotesters").map {
  dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.9"
)

scalacOptions += "-feature"
