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

  implementation(libs.buildscript.android)
  implementation(libs.buildscript.androidCacheFix)
  implementation(libs.buildscript.compose.jetbrains)
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.foojay)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.publish)
}
