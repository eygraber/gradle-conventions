package com.eygraber.conventions.compose

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.provider.Provider

class GradleConventionsCompose {
  var androidComposeCompilerVersionOverride: String? = null
  var enableAndroidCompilerMetrics: Boolean = false
  var applyToAndroidAndJvmOnly: Boolean = false
  var jetbrainsComposeCompilerOverride: MinimalExternalModuleDependency? = null
  var useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion: Boolean = false
  var suppressKotlinVersionCompatForJetbrains: String? = null

  @Suppress("ObjectPropertyNaming")
  companion object {
    const val JetbrainsCompilerArtifact = "org.jetbrains.compose.compiler:compiler"
    const val JetpackCompilerArtifact = "androidx.compose.compiler:compiler"
  }

  fun android(
    compilerVersionOverride: Provider<String>? = null,
    compilerOverride: Provider<MinimalExternalModuleDependency>? = null,
    enableAndroidCompilerMetrics: Boolean = false
  ) {
    compilerVersionOverride?.let { androidComposeCompilerVersionOverride = it.get() }
    compilerOverride?.let {
      val dependency = it.get()
      check(dependency.group == "androidx.compose.compiler" && dependency.name == "compiler") {
        "Only the $JetpackCompilerArtifact artifact should be used as an override"
      }
      val version = it.get().version
      requireNotNull(version) {
        "Please specify a version for the $JetpackCompilerArtifact artifact"
      }
      androidComposeCompilerVersionOverride = version
    }
    this.enableAndroidCompilerMetrics = enableAndroidCompilerMetrics
  }

  fun multiplatformWithAndroidCompiler(
    androidCompilerVersion: Provider<String>,
    applyToAndroidAndJvmOnly: Boolean = false,
    suppressKotlinVersion: String? = null
  ) {
    multiplatform(
      compilerMavenCoordinatesOverride = "$JetpackCompilerArtifact:$androidCompilerVersion",
      applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly,
      suppressKotlinVersion = suppressKotlinVersion
    )
  }

  fun multiplatformWithJetbrainsCompiler(
    jetbrainsCompilerVersion: Provider<String>,
    applyToAndroidAndJvmOnly: Boolean = false,
    suppressKotlinVersion: String? = null
  ) {
    multiplatform(
      compilerMavenCoordinatesOverride = "$JetbrainsCompilerArtifact:$jetbrainsCompilerVersion",
      applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly,
      suppressKotlinVersion = suppressKotlinVersion
    )
  }

  fun multiplatform(
    compilerMavenCoordinatesOverride: String? = null,
    compilerOverride: Provider<MinimalExternalModuleDependency>? = null,
    applyToAndroidAndJvmOnly: Boolean = false,
    suppressKotlinVersion: String? = null
  ) {
    if(compilerMavenCoordinatesOverride != null) {
      val coords = compilerMavenCoordinatesOverride.split(":")
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
    compilerOverride?.let { jetbrainsComposeCompilerOverride = it.get() }
    this.applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly
    suppressKotlinVersionCompatForJetbrains = suppressKotlinVersion
  }
}
