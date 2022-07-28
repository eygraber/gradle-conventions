package com.eygraber.gradle.repositories

import com.eygraber.ejson.Ejson
import com.eygraber.ejson.gradle.decryptSecrets
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.Actions
import java.io.File
import java.net.URI

@JvmOverloads
public fun RepositoryHandler.gitHubPackages(
  owner: String,
  repo: String,
  username: String,
  password: String,
  action: Action<MavenArtifactRepository> = Actions.doNothing()
) {
  maven { maven ->
    with(maven) {
      url = URI("https://maven.pkg.github.com/$owner/$repo")

      credentials { creds ->
        creds.username = username
        creds.password = password
      }
    }

    action.execute(maven)
  }
}

@JvmOverloads
public fun RepositoryHandler.gitHubPackages(
  owner: String,
  repo: String,
  username: String,
  ejsonSecretsFile: File,
  ejsonJsonKey: String = "github_packages_pat",
  ejsonPrivateKey: String? = null,
  ejsonPrivateKeyEnvVar: String = "EJSON_PRIVATE_KEY",
  action: Action<MavenArtifactRepository> = Actions.doNothing()
) {
  gitHubPackages(
    owner = owner,
    repo = repo,
    username = username,
    password = Ejson().decryptSecrets(
      secretsFile = ejsonSecretsFile.toPath(),
      userSuppliedPrivateKey = ejsonPrivateKey ?: System.getenv(ejsonPrivateKeyEnvVar)
    ) { json ->
      requireNotNull(
        json[ejsonJsonKey]
          ?.jsonPrimitive
          ?.contentOrNull
      )
    },
    action = action
  )
}
