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
    create("gradleUtils") {
      id = "com.eygraber.gradle.utils"
      implementationClass = "com.eygraber.gradle.GradleUtilsPlugin"
    }
  }
}

dependencies {
  api(project(":utils-base"))
  api(project(":utils-detekt"))
  api(project(":utils-kotlin"))

  implementation(libs.buildscript.android)
  implementation(libs.buildscript.androidCacheFix)
  implementation(libs.buildscript.compose.jetbrains)
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.foojay)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.publish)
}
