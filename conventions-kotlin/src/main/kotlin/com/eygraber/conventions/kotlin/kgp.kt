package com.eygraber.conventions.kotlin

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.DefaultKotlinBasePlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

public class KgpTestArgs {
  var minHeapSize: String? = null
  var maxHeapSize: String? = null

  var forkEvery: Long? = null
  var maxParallelForks: Int? = null

  var jvmArgs: List<String>? = null
}

public fun Project.configureKgp(
  jvmTargetVersion: Provider<JvmTarget>,
  jdkToolchainVersion: Provider<JavaLanguageVersion>,
  jvmDistribution: JvmVendorSpec? = null,
  allWarningsAsErrors: Boolean = true,
  explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled,
  configureJavaTargetVersion: Boolean = true,
  kotlinLanguageVersion: KotlinVersion? = null,
  kotlinApiVersion: KotlinVersion? = null,
  isProgressiveModeEnabled: Boolean = false,
  freeCompilerArgs: List<KotlinFreeCompilerArg> = emptyList(),
  testArgs: KgpTestArgs? = null,
  vararg optIns: KotlinOptIn,
): JavaVersion = configureKgp(
  jvmTargetVersion = jvmTargetVersion.orNull,
  jdkToolchainVersion = jdkToolchainVersion.orNull,
  jvmDistribution = jvmDistribution,
  allWarningsAsErrors = allWarningsAsErrors,
  explicitApiMode = explicitApiMode,
  configureJavaTargetVersion = configureJavaTargetVersion,
  kotlinLanguageVersion = kotlinLanguageVersion,
  kotlinApiVersion = kotlinApiVersion,
  isProgressiveModeEnabled = isProgressiveModeEnabled,
  freeCompilerArgs = freeCompilerArgs,
  testArgs = testArgs,
  optIns = optIns,
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
  testArgs: KgpTestArgs? = null,
  vararg optIns: KotlinOptIn,
): JavaVersion {
  val lowestSupportedJava = JavaVersion.VERSION_17
  val gradleVersion = GradleVersion.current()
  val highestSupportedJava: JavaVersion = when {
    gradleVersion >= GradleVersion.version("9.1.0") -> JavaVersion.VERSION_25
    gradleVersion >= GradleVersion.version("8.14") -> JavaVersion.VERSION_24
    gradleVersion >= GradleVersion.version("8.10") -> JavaVersion.VERSION_23
    gradleVersion >= GradleVersion.version("8.8") -> JavaVersion.VERSION_22
    gradleVersion >= GradleVersion.version("8.5") -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_20
  }

  val targetJavaVersion = jvmTargetVersion?.target?.let(JavaVersion::toVersion)
  val actualLowestSupportedJava: JavaVersion =
    if(targetJavaVersion == null) {
      lowestSupportedJava
    }
    else {
      when {
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
    tasks.withType(JavaCompile::class.java) { java ->
      java.sourceCompatibility = jvmTargetVersion.target
      java.targetCompatibility = jvmTargetVersion.target
    }
  }

  plugins.withType(DefaultKotlinBasePlugin::class.java) {
    val isKmp = this is KotlinMultiplatformPluginWrapper
    with(extensions.getByType(KotlinProjectExtension::class.java)) {
      when(explicitApiMode) {
        ExplicitApiMode.Strict -> explicitApi()
        ExplicitApiMode.Warning -> explicitApiWarning()
        ExplicitApiMode.Disabled -> explicitApi = null
      }

      if(jdkToolchainVersion == null) {
        if(JavaVersion.current() != buildJavaVersion) {
          jvmToolchain { toolchain ->
            toolchain.languageVersion.set(JavaLanguageVersion.of(buildJavaVersion.majorVersion.toInt()))
          }
        }
      }
      else {
        jvmToolchain { toolchain ->
          toolchain.languageVersion.set(jdkToolchainVersion)
          if(jvmDistribution != null) {
            toolchain.vendor.set(jvmDistribution)
          }
        }
      }

      if(isKmp) {
        sourceSets.configureEach { sourceSet ->
          for(optIn in optIns) {
            sourceSet.languageSettings.optIn(optIn.value)
          }
        }
      }

      if(this is HasConfigurableKotlinCompilerOptions<*>) {
        compilerOptions.freeCompilerArgs.addAll(
          freeCompilerArgs.map { freeCompilerArg -> freeCompilerArg.value },
        )
      }
    }

    tasks.withType(KotlinCompilationTask::class.java).configureEach { task ->
      with(task) {
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

          if(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9)) {
            compilerOptions.progressiveMode.set(isProgressiveModeEnabled)
          } else {
            if(isProgressiveModeEnabled) {
              compilerOptions.freeCompilerArgs.addAll(
                "-progressive",
              )
            }
          }
        }
        if(!isKmp) {
          if(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9)) {
            compilerOptions.optIn.addAll(optIns.map(KotlinOptIn::value))
          } else {
            compilerOptions.freeCompilerArgs.addAll(
              optIns.map { optIn -> "-opt-in=${optIn.value}" },
            )
          }
        }
      }
    }

    if(testArgs != null) {
      tasks.withType(Test::class.java).configureEach { testTask ->
        testArgs.minHeapSize?.let { testTask.minHeapSize = it }
        testArgs.maxHeapSize?.let { testTask.maxHeapSize = it }
        testArgs.forkEvery?.let { testTask.forkEvery = it }
        testArgs.maxParallelForks?.let { testTask.maxParallelForks = it }
        testArgs.jvmArgs?.let { testTask.jvmArgs = it }
      }
    }
  }

  return buildJavaVersion
}

public val Project.kotlinMultiplatform: KotlinMultiplatformExtension
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java)
