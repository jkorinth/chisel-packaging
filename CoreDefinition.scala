package chisel.packaging
import scala.io.Source
import play.api.libs.json._
import play.api.libs.functional.syntax._
import Chisel.Module

/**
 * Basic definition of a core for IP-XACT packaging.
 **/
class CoreDefinition(val name: String, val vendor: String, val library: String, val version: String,
                     val root: String, val postBuildActions: Seq[Module => Unit] = Seq()) {
  import CoreDefinition._
  def write(filename: String) : Boolean = try {
    val fw = new java.io.FileWriter(filename)
    fw.append(Json.toJson(this).toString)
    fw.flush()
    fw.close()
    true
  } catch { case ex: Exception => println("ERROR: " + ex); false }
}

/**
 * Contains methods for reading a core definition from Json.
 **/
object CoreDefinition {
  def apply(name: String, vendor: String, library: String, version: String, root: String): CoreDefinition =
    new CoreDefinition(name, vendor, library, version, root)

  def withActions(name: String, vendor: String, library: String, version: String, root: String,
            postBuildActions: Seq[Module => Unit]): CoreDefinition =
    new CoreDefinition(name, vendor, library, version, root, postBuildActions)

  def unapply(cd: CoreDefinition): Option[Tuple5[String, String, String, String, String]] =
    Some((cd.name, cd.vendor, cd.library, cd.version, cd.root))

  /** Provide automatic IP directory for given name. **/
  def root(name: String): String =
      java.nio.file.Paths.get(".").toAbsolutePath.resolveSibling("ip").resolve(name).toString

  implicit val coreDefinitionWrites : Writes[CoreDefinition] = (
      (JsPath \ "name").write[String] ~
      (JsPath \ "vendor").write[String] ~
      (JsPath \ "library").write[String] ~
      (JsPath \ "version").write[String] ~
      (JsPath \ "root").write[String]
    )(unlift(CoreDefinition.unapply))

  implicit val coreDefinitionReads : Reads[CoreDefinition] = (
      (JsPath \ "name").read[String] ~
      (JsPath \ "vendor").read[String] ~
      (JsPath \ "library").read[String] ~
      (JsPath \ "version").read[String] ~
      (JsPath \ "root").read[String]
    )(CoreDefinition.apply _)

  /**
   * Read CoreDefinition from file containing Json format.
   * @param filename Name (and path) of file.
   **/
  def read(filename: String) : Option[CoreDefinition] = try {
    val contents = Source.fromFile(filename).getLines.mkString("\n")
    val json = Json.parse(contents)
    json.validate[CoreDefinition] match {
      case s: JsSuccess[CoreDefinition] => Some(s.get)
      case e: JsError => { println("ERROR: " + e); None }
    }
  } catch { case ex: Exception => println("ERROR: " + ex); None }
}
