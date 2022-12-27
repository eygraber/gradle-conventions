package com.eygraber.conventions.compose

import org.gradle.api.provider.Provider

class GradleConventionsCompose {
  var applyToAndroidAndJvmOnly: Boolean = true
  var androidComposeCompilerVersionOverride: String? = null
  var androidComposeDependencyBomVersion: String? = null
  var useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion: Boolean = false

  fun overrideAndroidComposeVersions(
    compilerVersion: Provider<String>? = null,
    bomVersion: Provider<String>? = null
  ) {
    compilerVersion?.let { androidComposeCompilerVersionOverride = it.get() }
    bomVersion?.let { androidComposeDependencyBomVersion = it.get() }
  }

  fun overrideAndroidComposeVersions(
    compilerVersion: String? = null,
    bomVersion: String? = null
  ) {
    compilerVersion?.let { androidComposeCompilerVersionOverride = it }
    bomVersion?.let { androidComposeDependencyBomVersion = it }
  }
}
