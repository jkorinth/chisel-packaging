package chisel.packaging
import  chisel3._
import  scala.sys.process._

/** Module definition.
 *  @param config Optional, arbitrary configuration object, passed to post build actions.
 *  @param constr Module constructor function.
 *  @param core   Core definition.
 **/
final case class ModuleDef(config: Option[Any], constr: () => Module, core: CoreDefinition)

/**
 * Abstract IP-XACT builder class:
 * Objects can inherit from ModuleBuilder to automate the building
 * and packaging process. Provides main method that can be run
 * automatically via sbt run, takes arguments which cores to build.
 * @param packagingDir Base directory of packaging submodule
 *                     (default: ./packaging)
 **/
abstract class ModuleBuilder(packagingDir: String = "packaging") {
  val chiselArgs = Array[String]()
  /** List of modules to build. */
  val modules: Seq[ModuleDef]

  def main(args: Array[String]) {
    assert ((modules map (_.core.name.toLowerCase)).toSet.size == modules.length, "module names must be unique")
    val fm = modules filter (m => args.length == 0 || args.map(_.toLowerCase).contains(m.core.name.toLowerCase))
    assert (fm.length > 0, "no matching cores found for: " + args.mkString(", "))
    fm foreach { m =>
      Driver.execute(chiselArgs ++ Array("--target-dir", m.core.root), m.constr)
      m.core.postBuildActions map (fn => fn.apply(m.config))
      val json = "%s/%s.json".format(m.core.root, m.core.name)
      m.core.write(json)
      "%s/package.py %s".format(packagingDir, json).!
    }
  }
}
