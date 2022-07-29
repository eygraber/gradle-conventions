@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

dependencies {
  api(project(":utils-base"))

  compileOnly(libs.buildscript.kotlin)
}
