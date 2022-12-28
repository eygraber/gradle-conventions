plugins {
  `kotlin-dsl`
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlinDslPluginOptions {
  // need to override this because of ejson
  jvmTarget.set(libs.versions.jdk)
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
