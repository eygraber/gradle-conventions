import com.eygraber.conventions.kotlin.KotlinOptIn
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.detekt2)
    classpath(libs.buildscript.dokka)
    with(libs.buildscript.kotlin.get()) {
      classpath("$group:$name:$embeddedKotlinVersion")
    }
    classpath(libs.buildscript.publish)
  }
}

plugins {
  alias(libs.plugins.gradleConventions)
}

gradleConventionsDefaults {
  kotlin {
    jvmTargetVersion = JvmTarget.JVM_17
    allWarningsAsErrors = true
    optIns = setOf(KotlinOptIn.RequiresOptIn)
  }
}
