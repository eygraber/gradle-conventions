package com.eygraber.conventions.kotlin

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

public fun Project.configureKgp(
  jvmTargetVersion: Provider<JvmTarget?>,
  jdkToolchainVersion: Provider<JavaLanguageVersion?>,
  jvmDistribution: JvmVendorSpec? = null,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaTargetVersion: Boolean = true,
  kotlinLanguageVersion: KotlinVersion? = null,
  kotlinApiVersion: KotlinVersion? = null,
  isProgressiveModeEnabled: Boolean = false,
  freeCompilerArgs: List<KotlinFreeCompilerArg> = emptyList(),
  vararg optIns: KotlinOptIn
): JavaVersion = configureKgp(
  jvmTargetVersion = jvmTargetVersion.get(),
  jdkToolchainVersion.get(),
  jvmDistribution,
  allWarningsAsErrors,
  explicitApiMode,
  configureJavaTargetVersion,
  kotlinLanguageVersion,
  kotlinApiVersion,
  isProgressiveModeEnabled,
  freeCompilerArgs,
  *optIns
)

public fun Project.configureKgp(
  jvmTargetVersion: JvmTarget?,
  jdkToolchainVersion: JavaLanguageVersion?,
  jvmDistribution: JvmVendorSpec? = null,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaTargetVersion: Boolean = true,
  kotlinLanguageVersion: KotlinVersion? = null,
  kotlinApiVersion: KotlinVersion? = null,
  isProgressiveModeEnabled: Boolean = false,
  freeCompilerArgs: List<KotlinFreeCompilerArg> = emptyList(),
  vararg optIns: KotlinOptIn
): JavaVersion {
  val lowestSupportedJava = JavaVersion.VERSION_17
  val highestSupportedJava: JavaVersion = JavaVersion.VERSION_20

  val targetJavaVersion = jvmTargetVersion?.target?.toInt()?.let(JavaVersion::toVersion)
  val actualLowestSupportedJava: JavaVersion = when(targetJavaVersion) {
    null -> lowestSupportedJava
    else -> when {
      targetJavaVersion > lowestSupportedJava -> targetJavaVersion
      else -> lowestSupportedJava
    }
  }

  require(actualLowestSupportedJava <= highestSupportedJava) {
    val error = """
    |The detected lowest supported version of Java ($actualLowestSupportedJava) is greater than the
    |detected highest supported version of Java ($highestSupportedJava)
    """.trimMargin()

    if(targetJavaVersion != null && targetJavaVersion > lowestSupportedJava) {
      "$error because jvmTargetVersion is $jvmTargetVersion"
    }
    else {
      error
    }
  }

  val buildJavaVersion: JavaVersion = when {
    JavaVersion.current() < actualLowestSupportedJava -> actualLowestSupportedJava
    JavaVersion.current() > highestSupportedJava -> highestSupportedJava
    else -> JavaVersion.current()
  }

  if(configureJavaTargetVersion && jvmTargetVersion != null) {
    tasks.withType(JavaCompile::class.java) {
      sourceCompatibility = jvmTargetVersion.target
      targetCompatibility = jvmTargetVersion.target
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

      if(jdkToolchainVersion == null) {
        if(JavaVersion.current() != buildJavaVersion) {
          plugins.withType(JavaBasePlugin::class.java) {
            jvmToolchain {
              languageVersion.set(JavaLanguageVersion.of(buildJavaVersion.majorVersion.toInt()))
            }
          }
        }
      }
      else {
        plugins.withType(JavaBasePlugin::class.java) {
          jvmToolchain {
            languageVersion.set(jdkToolchainVersion)
            if(jvmDistribution != null) {
              vendor.set(jvmDistribution)
            }
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

    tasks.withType(KotlinCompilationTask::class.java).configureEach {
      compilerOptions.allWarningsAsErrors.set(allWarningsAsErrors)
      if(this is KotlinJvmCompile) {
        if(jvmTargetVersion != null) {
          compilerOptions.jvmTarget.set(jvmTargetVersion)
        }
        if(kotlinLanguageVersion != null) {
          compilerOptions.languageVersion.set(kotlinLanguageVersion)
        }
        if(kotlinApiVersion != null) {
          compilerOptions.apiVersion.set(kotlinApiVersion)
        }

        compilerOptions.progressiveMode.set(isProgressiveModeEnabled)
      }
      compilerOptions.freeCompilerArgs.addAll(
        freeCompilerArgs.map { freeCompilerArg -> freeCompilerArg.value }
      )
      if(!isKmp) {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        if(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9)) {
          compilerOptions.optIn.addAll(optIns.map(KotlinOptIn::value))
        } else {
          compilerOptions.freeCompilerArgs.addAll(
            optIns.map { optIn -> "-opt-in=${optIn.value}" }
          )
        }
      }
    }
  }

  return buildJavaVersion
}

public val Project.kotlinMultiplatform: KotlinMultiplatformExtension
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java)
