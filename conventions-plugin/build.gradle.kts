import com.eygraber.gradle.kotlin.configureKgp

plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.publish")
}

configureKgp(
  jdkVersion = libs.versions.jdk,
  optIns = arrayOf("kotlin.RequiresOptIn")
)

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
