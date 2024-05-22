package com.eygraber.conventions.repositories

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.Actions
import org.gradle.kotlin.dsl.maven

public fun RepositoryHandler.addCommonRepositories(
  includeMavenCentral: Boolean = true,
  mavenCentral: Action<MavenArtifactRepository> = Actions.doNothing(),
  includeMavenCentralSnapshots: Boolean = false,
  mavenCentralSnapshots: Action<MavenArtifactRepository> = Actions.doNothing(),
  includeGoogle: Boolean = false,
  google: Action<MavenArtifactRepository> = Actions.doNothing(),
  includeJetbrainsComposeDev: Boolean = false,
  jetbrainsComposeDev: Action<MavenArtifactRepository> = Actions.doNothing(),
  includeJitpack: Boolean = false,
  jitpack: Action<MavenArtifactRepository> = Actions.doNothing(),
  includeGradlePluginPortal: Boolean = false,
  gradlePluginPortal: Action<ArtifactRepository> = Actions.doNothing(),
) {
  if(includeGoogle) {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }

      google.execute(this)
    }
  }

  if(includeJetbrainsComposeDev) {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      content {
        includeGroupByRegex("org\\.jetbrains.*")
      }

      jetbrainsComposeDev.execute(this)
    }
  }

  if(includeMavenCentralSnapshots) {
    mavenCentralSnapshots(mavenCentralSnapshots)
    mavenCentralSnapshotsS01(mavenCentralSnapshots)
  }

  if(includeMavenCentral) {
    mavenCentral(mavenCentral)
  }

  if(includeJitpack) {
    maven("https://jitpack.io") {
      jitpack.execute(this)
    }
  }

  if(includeGradlePluginPortal) {
    gradlePluginPortal(gradlePluginPortal)
  }
}
