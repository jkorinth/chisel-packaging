name := "chisel-packaging"

organization := "esa.cs.tu-darmstadt.de"

version := "0.3-SNAPSHOT"

scalaVersion := "2.11.11"

unmanagedResources in Compile ++= Seq(
  baseDirectory.value / "package.py",
  baseDirectory.value / "axi4.py"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
val defaultVersions = Map("chisel3"          -> "3.1-SNAPSHOT",
                          "chisel-iotesters" -> "1.2-SNAPSHOT")

libraryDependencies ++= (Seq("chisel3","chisel-iotesters").map {
  dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.3"
)

scalacOptions += "-feature"
