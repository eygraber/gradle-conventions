plugins {
  `kotlin-dsl`
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  api(project(":conventions-base"))
  api(project(":conventions-kotlin"))

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
