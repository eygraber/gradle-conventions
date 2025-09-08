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
  api(projects.conventionsBase)
  api(projects.conventionsDetekt)
  api(projects.conventionsKotlin)

  compileOnly(libs.buildscript.android)
  compileOnly(libs.buildscript.androidCacheFix)
  compileOnly(libs.buildscript.compose.compiler)
  compileOnly(libs.buildscript.compose.jetbrains)
  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.dokka)
  compileOnly(libs.buildscript.kotlin)
  compileOnly(libs.buildscript.publish)
}
