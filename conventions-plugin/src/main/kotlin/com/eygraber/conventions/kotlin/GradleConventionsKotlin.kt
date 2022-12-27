package com.eygraber.conventions.kotlin

import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

class GradleConventionsKotlin {
  var jdkVersion: String? = null
  var jvmDistribution: JvmVendorSpec? = null
  var allWarningsAsErrors: Boolean = true
  var explicitApiMode: ExplicitApiMode = ExplicitApiMode.Disabled
  var configureJava: Boolean = false
  var useK2: Boolean = false
  var freeCompilerArgs: Set<KotlinFreeCompilerArg> = emptySet()
  var optIns: Set<KotlinOptIn> = emptySet()
}
