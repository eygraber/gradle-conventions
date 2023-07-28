package com.eygraber.conventions.kotlin

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
  var configureJava: Boolean = true
  var useK2: Boolean = false
  var freeCompilerArgs: Set<KotlinFreeCompilerArg> = emptySet()
  var optIns: Set<KotlinOptIn> = emptySet()
}
