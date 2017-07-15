package chisel.packaging
import  chisel3._
import  CoreDefinition._

object ModuleBuilderTest extends ModuleBuilder(".") {
  private object TestModule extends Module { val io = IO(new Bundle) }

  val modules: List[(() => Module, CoreDefinition)] = List(
    ( // test module
      () => TestModule,
      CoreDefinition(
        name    = "TestModule",
        vendor  = "esa.cs.tu-darmstadt.de",
        library = "chisel",
        version = "1.0",
        root("TestModule")
      )
    )
  )
}
