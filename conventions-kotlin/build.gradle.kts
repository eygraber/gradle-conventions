plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

dependencies {
  api(project(":conventions-base"))

  compileOnly(libs.buildscript.kotlin)
}
