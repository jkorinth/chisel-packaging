package chisel.packaging
import Chisel._
import scala.sys.process._

/**
 * Abstract IP-XACT builder class:
 * Objects can inherit from ModuleBuilder to automate the building
 * and packaging process. Provides main method that can be run
 * automatically via sbt run, takes arguments which cores to build.
 * @param packagingDir Base directory of packaging submodule
 *                     (default: ./packaging)
 **/
abstract class ModuleBuilder(packagingDir: String = "packaging") {
  val chiselArgs = Array("--backend", "v", "--compile")
  val modules: List[(() => Module, CoreDefinition)]

  def main(args: Array[String]) {
    assert ((modules map (_._2.name.toLowerCase)).toSet.size == modules.length, "module names must be unique")
    val fm = modules filter (m => args.length == 0 || args.map(_.toLowerCase).contains(m._2.name.toLowerCase))
    assert (fm.length > 0, "no matching cores found for: " + args.mkString(", "))
    fm foreach { m =>
      chiselMain(chiselArgs ++ Array("--targetDir", m._2.root), m._1)
      val json = "%s/%s.json".format(m._2.root, m._2.name)
      m._2.write(json)
      "%s/package.py %s".format(packagingDir, json) !
    }
  }
}
