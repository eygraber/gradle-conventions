plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  api(projects.conventionsBase)
  api(projects.conventionsKotlin)

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
