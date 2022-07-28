package com.eygraber.gradle.publishing

import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

public fun RepositoryHandler.githubPackagesPublishing(
  owner: String,
  repo: String
) {
  maven { maven ->
    with(maven) {
      name = "githubPackages"

      url = URI("https://maven.pkg.github.com/isapp/$owner/$repo")

      credentials { creds ->
        with(creds) {
          username = System.getenv("GITHUB_ACTOR")
          password = System.getenv("GITHUB_TOKEN")
        }
      }
    }
  }
}
