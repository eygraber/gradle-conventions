package com.eygraber.conventions.kotlin

import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class GradleConventionsKotlin {
  var jvmTargetVersion: JvmTarget? = null
  var jdkToolchainVersion: JavaLanguageVersion? = null
  var jvmDistribution: JvmVendorSpec? = null
  var allWarningsAsErrors: Boolean = true
  var explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled
  var configureJavaTargetVersion: Boolean = true
  var languageVersion: KotlinVersion? = null
  var apiVersion: KotlinVersion? = null
  var isProgressiveModeEnabled: Boolean = false
  var freeCompilerArgs: Set<KotlinFreeCompilerArg> = emptySet()
  var optIns: Set<KotlinOptIn> = emptySet()

  var kotlinVersion: KotlinVersion = KotlinVersion.CURRENT
    set(value) {
      field = value
      languageVersion = value
      apiVersion = value
    }

  fun Provider<String>.toJvmTarget() = JvmTarget.fromTarget(get())
}
