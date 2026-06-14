plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  api(projects.conventionsBase)
  api(projects.conventionsKotlin)

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.detekt2) {
    // The Gradle API is already provided by the kotlin-dsl plugin via gradleApi()
    exclude(group = "org.gradle.experimental", module = "gradle-public-api")
  }
  compileOnly(libs.buildscript.kotlin)
}
