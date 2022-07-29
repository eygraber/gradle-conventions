@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

// configureKgp(
//   jdkVersion = libs.versions.jdk,
//   explicitApiMode = ExplicitApiMode.Strict
// )

dependencies {
  implementation(libs.buildscript.ejson)
  implementation(libs.kotlinx.serialization.json)
}
