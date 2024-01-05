import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.publishing.githubPackagesPublishing

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

val gitHubDefaults = gradleConventionsDefaultsService.github

@Suppress("LabeledExpression")
with(gradleConventionsExtension) {
  with(github) {
    owner = gitHubDefaults.owner
    repoName = gitHubDefaults.repoName
  }

  awaitGitHubConfigured { isUserConfigured ->
    if((owner == null || repoName == null) && !isUserConfigured) return@awaitGitHubConfigured

    publishing {
      repositories.githubPackagesPublishing(
        owner = requireNotNull(owner) {
          "Please set owner in the gradleConventions github extension"
        },
        repo = requireNotNull(repoName) {
          "Please set repoName in the gradleConventions github extension"
        },
      )
    }

    mavenPublishing {
      signAllPublications()
    }
  }
}
