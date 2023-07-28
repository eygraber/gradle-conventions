package com.eygraber.conventions.compose

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.provider.Provider

class GradleConventionsCompose {
  var androidComposeCompilerVersionOverride: String? = null
  var androidComposeDependencyBomVersion: String? = null
  var enableAndroidCompilerMetrics: Boolean = false
  var ignoreNonJvmTargets: Boolean = false
  var jetbrainsComposeCompilerOverride: MinimalExternalModuleDependency? = null
  var useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion: Boolean = false

  fun android(
    compilerVersion: Provider<String>? = null,
    bomVersion: Provider<String>? = null,
    enableAndroidCompilerMetrics: Boolean = false
  ) {
    compilerVersion?.let { androidComposeCompilerVersionOverride = it.get() }
    bomVersion?.let { androidComposeDependencyBomVersion = it.get() }
    this.enableAndroidCompilerMetrics = enableAndroidCompilerMetrics
  }

  fun android(
    compilerVersion: String? = null,
    bomVersion: String? = null,
    enableAndroidCompilerMetrics: Boolean = false
  ) {
    compilerVersion?.let { androidComposeCompilerVersionOverride = it }
    bomVersion?.let { androidComposeDependencyBomVersion = it }
    this.enableAndroidCompilerMetrics = enableAndroidCompilerMetrics
  }

  fun multiplatform(
    compilerMavenCoordinates: String? = null,
    ignoreNonJvmTargets: Boolean = false
  ) {
    if(compilerMavenCoordinates != null) {
      val coords = compilerMavenCoordinates.split(":")
      jetbrainsComposeCompilerOverride = when(coords.size) {
        3 -> DefaultMinimalDependency(
          DefaultModuleIdentifier.newId(
            coords[0],
            coords[1]
          ),
          DefaultMutableVersionConstraint(coords[2])
        )

        2 -> DefaultMinimalDependency(
          DefaultModuleIdentifier.newId(
            coords[0],
            coords[1]
          ),
          DefaultMutableVersionConstraint(DefaultImmutableVersionConstraint.of())
        )

        else -> error("Please specify full maven coordinates for the Compose compiler you'd like to use")
      }
    }
    this.ignoreNonJvmTargets = ignoreNonJvmTargets
  }

  fun multiplatform(
    compiler: Provider<MinimalExternalModuleDependency>? = null,
    ignoreNonJvmTargets: Boolean = false
  ) {
    compiler?.let { jetbrainsComposeCompilerOverride = it.get() }
    this.ignoreNonJvmTargets = ignoreNonJvmTargets
  }

  fun multiplatform(
    compiler: MinimalExternalModuleDependency? = null,
    ignoreNonJvmTargets: Boolean = false
  ) {
    compiler?.let { jetbrainsComposeCompilerOverride = it }
    this.ignoreNonJvmTargets = ignoreNonJvmTargets
  }
}
