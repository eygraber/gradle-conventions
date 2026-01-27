plugins {
  `kotlin-dsl`
  id("com.eygraber.conventions-kotlin-library")
  // id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

gradlePlugin {
  plugins {
    create("gradleConventionsSettings") {
      id = "com.eygraber.conventions.settings"
      implementationClass = "com.eygraber.conventions.settings.GradleConventionsSettingsPlugin"
    }
  }
}

dependencies {
  implementation(libs.buildscript.ejson)
  implementation(libs.kotlinx.serialization.json)
}
