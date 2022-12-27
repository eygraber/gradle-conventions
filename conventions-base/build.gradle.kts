plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

kotlinDslPluginOptions {
  // need to override this because of ejson
  jvmTarget.set(libs.versions.jdk)
}

dependencies {
  implementation(libs.buildscript.ejson)
  implementation(libs.kotlinx.serialization.json)
}
