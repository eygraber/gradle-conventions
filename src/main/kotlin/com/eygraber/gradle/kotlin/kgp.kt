package com.eygraber.gradle.kotlin

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public fun Project.setupKgp(
  jdkVersion: String,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  vararg optIns: String
) {
  plugins.withType(KotlinBasePluginWrapper::class.java) {
    with(extensions.getByType(KotlinProjectExtension::class.java)) {
      when(explicitApiMode) {
        ExplicitApiMode.Strict -> explicitApi()
        ExplicitApiMode.Warning -> explicitApiWarning()
        ExplicitApiMode.Disabled -> {}
      }

      jvmToolchain { toolchain ->
        with(toolchain) {
          languageVersion.set(JavaLanguageVersion.of(jdkVersion))
          vendor.set(JvmVendorSpec.AZUL)
        }
      }

      sourceSets.configureEach { sourceSet ->
        with(sourceSet.languageSettings) {
          for(optIn in optIns) {
            optIn(optIn)
          }
        }
      }
    }

    tasks.withType(KotlinCompile::class.java).configureEach { kotlin ->
      with(kotlin) {
        kotlinOptions.allWarningsAsErrors = allWarningsAsErrors
        kotlinOptions.jvmTarget = jdkVersion
      }
    }
  }
}
