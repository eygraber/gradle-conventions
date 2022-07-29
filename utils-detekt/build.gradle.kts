@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

dependencies {
  api(project(":utils-base"))
  api(project(":utils-kotlin"))

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
