import com.eygraber.gradle.gradleUtilsDefaultsService
import com.eygraber.gradle.gradleUtilsExtension
import com.eygraber.gradle.publishing.githubPackagesPublishing

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

val gitHubDefaults = gradleUtilsDefaultsService.github

@Suppress("LabeledExpression")
with(gradleUtilsExtension) {
  with(github) {
    owner = gitHubDefaults.owner
    repoName = gitHubDefaults.repoName
  }

  awaitGitHubConfigured { isUserConfigured ->
    if((owner == null || repoName == null) && !isUserConfigured) return@awaitGitHubConfigured

    publishing {
      repositories.githubPackagesPublishing(
        owner = requireNotNull(owner) {
          "Please set owner in the gradleUtils github extension"
        },
        repo = requireNotNull(repoName) {
          "Please set repoName in the gradleUtils github extension"
        }
      )
    }

    mavenPublishing {
      @Suppress("UnstableApiUsage")
      signAllPublications()
    }
  }
}
