package com.eygraber.gradle.publishing

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.Actions
import java.net.URI

@JvmOverloads
public fun RepositoryHandler.githubPackagesPublishing(
  owner: String,
  repo: String,
  action: Action<MavenArtifactRepository> = Actions.doNothing()
) {
  maven { maven ->
    with(maven) {
      name = "githubPackages"

      url = URI("https://maven.pkg.github.com/$owner/$repo")

      credentials { creds ->
        with(creds) {
          username = System.getenv("GITHUB_ACTOR")
          password = System.getenv("GITHUB_TOKEN")
        }
      }
    }

    action.execute(maven)
  }
}
