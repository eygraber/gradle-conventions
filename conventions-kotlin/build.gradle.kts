plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  // id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  api(project(":conventions-base"))

  compileOnly(libs.buildscript.kotlin)
}
