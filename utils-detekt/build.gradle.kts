plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

gradlePlugin {
  plugins {
    create("gradleUtilsDetekt") {
      id = "com.eygraber.gradle.utils.detekt"
      implementationClass = "com.eygraber.gradle.detekt.GradleUtilsDetektPlugin"
    }
  }
}

dependencies {
  api(project(":utils-base"))
  api(project(":utils-kotlin"))

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
