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

gradlePlugin {
  plugins {
    create("gradleUtilsBase") {
      id = "com.eygraber.gradle.utils.base"
      implementationClass = "com.eygraber.gradle.GradleUtilsBasePlugin"
    }

    create("gradleUtilsSettings") {
      id = "com.eygraber.gradle.utils.settings"
      implementationClass = "com.eygraber.gradle.settings.GradleUtilsSettingsPlugin"
    }
  }
}

dependencies {
  implementation(libs.buildscript.ejson)
  implementation(libs.kotlinx.serialization.json)
}
