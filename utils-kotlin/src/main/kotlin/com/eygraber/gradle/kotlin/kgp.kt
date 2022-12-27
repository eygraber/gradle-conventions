package com.eygraber.gradle.kotlin

import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public fun Project.configureKgp(
  jdkVersion: Provider<String>,
  jvmDistribution: JvmVendorSpec? = null,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaCompatibility: Boolean = true,
  useK2: Boolean = false,
  freeCompilerArgs: List<KotlinFreeCompilerArg> = emptyList(),
  vararg optIns: KotlinOptIn
) {
  configureKgp(
    jdkVersion.get(),
    jvmDistribution,
    allWarningsAsErrors,
    explicitApiMode,
    configureJavaCompatibility,
    useK2,
    freeCompilerArgs,
    *optIns
  )
}

public fun Project.configureKgp(
  jdkVersion: String,
  jvmDistribution: JvmVendorSpec? = null,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaCompatibility: Boolean = true,
  useK2: Boolean = false,
  freeCompilerArgs: List<KotlinFreeCompilerArg> = emptyList(),
  vararg optIns: KotlinOptIn
) {
  if(configureJavaCompatibility) {
    tasks.withType(JavaCompile::class.java) {
      sourceCompatibility = jdkVersion
      targetCompatibility = jdkVersion
    }
  }

  plugins.withType(KotlinBasePluginWrapper::class.java) {
    val isKmp = this is KotlinMultiplatformPluginWrapper
    with(extensions.getByType(KotlinProjectExtension::class.java)) {
      when(explicitApiMode) {
        ExplicitApiMode.Strict -> explicitApi()
        ExplicitApiMode.Warning -> explicitApiWarning()
        ExplicitApiMode.Disabled -> explicitApi = null
      }

      plugins.withType(JavaBasePlugin::class.java) {
        jvmToolchain {
          languageVersion.set(JavaLanguageVersion.of(jdkVersion.removePrefix("1.")))
          if(jvmDistribution != null) {
            vendor.set(jvmDistribution)
          }
        }
      }

      if(isKmp) {
        sourceSets.configureEach {
          for(optIn in optIns) {
            languageSettings.optIn(optIn.value)
          }
        }
      }
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
      kotlinOptions.allWarningsAsErrors = allWarningsAsErrors
      kotlinOptions.jvmTarget = jdkVersion
      kotlinOptions.useK2 = useK2
      kotlinOptions.freeCompilerArgs += freeCompilerArgs.map { freeCompilerArg -> freeCompilerArg.value }
      if(!isKmp) kotlinOptions.freeCompilerArgs += optIns.map { optIn -> "-opt-in=${optIn.value}" }
    }
  }
}

public val Project.kotlinMultiplatform: KotlinMultiplatformExtension
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java)
