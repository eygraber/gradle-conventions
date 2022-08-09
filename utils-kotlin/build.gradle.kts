plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

gradlePlugin {
  plugins {
    create("gradleUtilsKotlin") {
      id = "com.eygraber.gradle.utils.kotlin"
      implementationClass = "com.eygraber.gradle.kotlin.GradleUtilsKotlinPlugin"
    }
  }
}

dependencies {
  api(project(":utils-base"))

  compileOnly(libs.buildscript.kotlin)
}
