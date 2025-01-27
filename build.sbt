import org.scalajs.linker.interface.ModuleSplitStyle
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.learn"
ThisBuild / scalaVersion := "3.6.2"

val scala3Version = "3.6.2"
val learnPackage = "com.learn"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZioFullStack"
  )
  .aggregate(server)

lazy val common = (crossProject(JSPlatform, JVMPlatform) in file("common"))
  .settings(
    name         := "common",
    scalaVersion := scala3Version,
    organization := learnPackage,
    libraryDependencies += "dev.zio" %% "zio-schema" % "1.1.1",
    libraryDependencies += "dev.zio" %% "zio-schema-json" % "1.1.1",
  )

lazy val server = project.in(file("server"))
  .settings(
    name         := "server",
    scalaVersion := scala3Version,
    organization := learnPackage,
    Compile / run / mainClass := Some("com.learn.Main"),
    libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC6",
  )
  .dependsOn(common.jvm)

lazy val client = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("client")))
    },

    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13),
    libraryDependencies += "com.raquo" %%% "laminar" % "15.0.1",
//    libraryDependencies += "common" %%% "common" % "0.1.0",
    libraryDependencies += "dev.zio" %%% "zio-schema" % "1.1.1",
    libraryDependencies += "dev.zio" %%% "zio-schema-json" % "1.1.1",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0" % Test,

    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings   := true,

    externalNpm := baseDirectory.value
  )
  .dependsOn(common.js)