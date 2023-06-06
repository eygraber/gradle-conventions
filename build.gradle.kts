import com.eygraber.conventions.kotlin.KotlinOptIn

buildscript {
  dependencies {
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  alias(libs.plugins.gradleConventions)
}

gradleConventionsDefaults {
  detekt {
    plugins(
      libs.detektEygraber.formatting,
      libs.detektEygraber.style
    )
  }
  kotlin {
    jdkVersion = libs.versions.jdk.get()
    jvmDistribution = JvmVendorSpec.AZUL
    allWarningsAsErrors = true
    optIns = setOf(KotlinOptIn.RequiresOptIn)
  }
}
