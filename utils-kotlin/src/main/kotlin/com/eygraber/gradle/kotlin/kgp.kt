package com.eygraber.gradle.kotlin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public fun Project.configureKgp(
  jdkVersion: Provider<String>,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaCompatibility: Boolean = true,
  vararg optIns: String
) {
  configureKgp(
    jdkVersion.get(),
    allWarningsAsErrors,
    explicitApiMode,
    configureJavaCompatibility,
    *optIns
  )
}

public fun Project.configureKgp(
  jdkVersion: String,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaCompatibility: Boolean = true,
  vararg optIns: String
) {
  if(configureJavaCompatibility) {
    tasks.withType(JavaCompile::class.java) {
      sourceCompatibility = jdkVersion
      targetCompatibility = jdkVersion
    }
  }

  plugins.withType(KotlinBasePluginWrapper::class.java) {
    with(extensions.getByType(KotlinProjectExtension::class.java)) {
      when(explicitApiMode) {
        ExplicitApiMode.Strict -> explicitApi()
        ExplicitApiMode.Warning -> explicitApiWarning()
        ExplicitApiMode.Disabled -> {}
      }

      jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkVersion))
        vendor.set(JvmVendorSpec.AZUL)
      }

      sourceSets.configureEach {
        for(optIn in optIns) {
          languageSettings.optIn(optIn)
        }
      }
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
      kotlinOptions.allWarningsAsErrors = allWarningsAsErrors
      kotlinOptions.jvmTarget = jdkVersion
    }
  }
}
