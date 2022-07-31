package com.eygraber.gradle.settings

import com.eygraber.gradle.repositories.gitHubPackages
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings
import org.gradle.internal.Actions
import java.io.File

public abstract class GitHubPackagesRepositoryPlugin : Plugin<Settings> {
  public abstract class Extension {
    public fun Settings.addTo(
      pluginManagement: Boolean,
      dependencyResolutionManagement: Boolean,
      owner: String,
      repo: String,
      username: String,
      password: String,
      action: Action<MavenArtifactRepository> = Actions.doNothing()
    ) {
      ArrayList<RepositoryHandler>(2).apply {
        if(pluginManagement) add(settings.pluginManagement.repositories)

        @Suppress("UnstableApiUsage")
        if(dependencyResolutionManagement) add(settings.dependencyResolutionManagement.repositories)
      }.forEach { handler ->
        handler.gitHubPackages(
          owner = owner,
          repo = repo,
          username = username,
          password = password,
          action = action
        )
      }
    }

    public fun Settings.addTo(
      pluginManagement: Boolean,
      dependencyResolutionManagement: Boolean,
      owner: String,
      repo: String,
      username: String,
      ejsonSecretsFile: File,
      ejsonJsonKey: String = "github_packages_pat",
      ejsonPrivateKey: String? = null,
      ejsonPrivateKeyEnvVar: String = "EJSON_PRIVATE_KEY",
      action: Action<MavenArtifactRepository> = Actions.doNothing()
    ) {
      ArrayList<RepositoryHandler>(2).apply {
        if(pluginManagement) add(settings.pluginManagement.repositories)

        @Suppress("UnstableApiUsage")
        if(dependencyResolutionManagement) add(settings.dependencyResolutionManagement.repositories)
      }.forEach { handler ->
        handler.gitHubPackages(
          owner = owner,
          repo = repo,
          username = username,
          ejsonSecretsFile = ejsonSecretsFile,
          ejsonJsonKey = ejsonJsonKey,
          ejsonPrivateKey = ejsonPrivateKey,
          ejsonPrivateKeyEnvVar = ejsonPrivateKeyEnvVar,
          action = action
        )
      }
    }
  }

  override fun apply(target: Settings) {
    target.extensions.create("gitHubPackagesRepository", Extension::class.java)
  }
}
