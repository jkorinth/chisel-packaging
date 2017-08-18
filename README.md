Chisel IP-XACT packaging for Xilinx Vivado Design Suite
=======================================================

Helper scripts and Scala classes to simplify the generation of IP-XACT IP cores from Chisel.
Uses [*Xilinx Vivado Design Suite*] [2] to infer most of the interfaces automatically.
The resulting `component.xml` files can be edited manually or with Vivado, if the automagic
did not fit your needs.

Requirements
----------------------

*   Vivado 2016.x+
*   Chisel 3.x *(currently using `3.0-SNAPSHOT`)*
*   *optional*: sbt

Basic Usage
-----------

Class `ModuleBuilder` provides an abstract base class for an executable object with a `main`
method. You'll only need to provide a list of Chisel cores you'd like to wrap into IP-XACT.
A full example can be found in `example`; test it as follows:

1.   Source the Vivado settings scripts, make sure `vivado` is in the `PATH`.
2.   In the `chisel-packaging` base dir, do:  
      
        cp example/* .
        
3.   Execute via:
        
        sbt run
        
4.   This should create a new directory `ip`, which contains subdirectories for each core.
     The subdirectories contain the Verilog sources and the IP-XACT `component.xml`.

You can use the `ip` directory as a base directory for user IP in Vivado, the cores should
automatically appear in Vivado and IP-Integrator.


Usage in Chisel Projects (*sbt*)
--------------------------------

The easiest way to setup `chisel-packaging` for your Chisel project with `sbt` is using
**git subtree** ([this article] [1] has an excellent intro to subtrees) as follows:

1.   In your the base directory of your project, add a new remote *chisel-packaging*:
        
        git remote add chisel-packaging https://bitbucket.org/jkorinth/chisel-packaging.git
        
2.   Setup a subtree in `packaging`:
       
        git subtree add --prefix packaging chisel-packaging master --squash
      
    This will clone the `chisel-packaging` into the `packaging` folder.
    
3.   Add a project dependency in your `build.sbt`, add lines:
        
        lazy val packaging = project.in(file("packaging"))
        
        lazy val root = (project in file(".")).dependsOn(packaging)
        
    
    *Note that the empty lines are **not optional** - a quirk of sbt.*
    
4.   Implement `chisel.packaging.ModuleBuilder` in your own code; if you've chosen
     a different directory than `packaging` for the subtree, you can pass it to the
     baseclass constructor -- see [example/ModuleBuilderTest.scala][3]
     
5.   Build the IP-XACT cores via `sbt run`.

Gotchas
-------

*   The python script requires `vivado` to be in `PATH`, so you need to source the Vivado
    settings scripts first.
*   The repo is setup for Chisel 3.x; it can be used for Chisel 2.x, but that requires
    manual changes - open an issue in the issue tracker if you need it.
*   If you're using the `sbt` approach outlined above, make sure your `build.sbt` contains
    the empty lines; they are required by `sbt`.

[1]: https://www.atlassian.com/blog/git/alternatives-to-git-submodule-git-subtree
[2]: https://www.xilinx.com/products/design-tools/vivado.html
[3]: example/ModuleBuilderTest.scala
