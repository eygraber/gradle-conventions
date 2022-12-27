plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

dependencies {
  api(project(":conventions-base"))
  api(project(":conventions-kotlin"))

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
