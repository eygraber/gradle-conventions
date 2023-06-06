plugins {
  `kotlin-dsl`
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

gradlePlugin {
  plugins {
    create("gradleConventions") {
      id = "com.eygraber.conventions"
      implementationClass = "com.eygraber.conventions.GradleConventionsPlugin"
    }
  }
}

dependencies {
  api(project(":conventions-base"))
  api(project(":conventions-detekt"))
  api(project(":conventions-kotlin"))

  compileOnly(libs.buildscript.android)
  compileOnly(libs.buildscript.androidCacheFix)
  compileOnly(libs.buildscript.compose.jetbrains)
  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.dokka)
  compileOnly(libs.buildscript.kotlin)
  compileOnly(libs.buildscript.publish)
}
