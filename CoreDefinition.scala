package chisel.packaging
import scala.io.Source
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Basic definition of a core for IP-XACT packaging.
 **/
case class CoreDefinition(name: String, vendor: String, library: String, version: String, root: String) {
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
  /** Alternative constructor: supplies root dir as ip/<name>. **/
  def apply(name: String, vendor: String, library: String, version: String) = CoreDefinition(
      name,
      vendor,
      library,
      version,
      java.nio.file.Paths.get(".").toAbsolutePath.resolveSibling("ip").resolve(name).toString
    )

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
