import com.eygraber.gradle.gradleUtilsDefaultsService
import com.eygraber.gradle.gradleUtilsExtension
import com.eygraber.gradle.kotlin.kmp.spm.registerPublishSpmToMavenTasks
import com.eygraber.gradle.publishing.githubPackagesPublishing

plugins {
  `maven-publish`
}

val gitHubDefaults = gradleUtilsDefaultsService.github
val spmDefaults = gradleUtilsDefaultsService.spm

@Suppress("LabeledExpression")
with(gradleUtilsExtension) {
  with(github) {
    owner = gitHubDefaults.owner
    repoName = gitHubDefaults.repoName
  }

  with(spm) {
    frameworkName = spmDefaults.frameworkName
    version = spmDefaults.version
    includeMacos = spmDefaults.includeMacos
  }

  awaitGitHubConfigured { isGitHubUserConfigured ->
    if((owner == null || repoName == null) && !isGitHubUserConfigured) return@awaitGitHubConfigured

    val owner = requireNotNull(owner) {
      "Please set owner in the gradleUtils github extension"
    }

    val repoName = requireNotNull(repoName) {
      "Please set repoName in the gradleUtils github extension"
    }

    if(owner.isNotBlank() && repoName.isNotBlank()) {
      publishing {
        repositories.githubPackagesPublishing(
          owner = owner,
          repo = repoName
        )
      }

      awaitSpmConfigured { isSpmUserConfigured ->
        if((frameworkName == null || version == null) && !isSpmUserConfigured) return@awaitSpmConfigured

        val frameworkName = requireNotNull(frameworkName) {
          "Please set frameworkName in the gradleUtils spm extension"
        }

        val version = requireNotNull(version) {
          "Please set version in the gradleUtils spm extension"
        }

        registerPublishSpmToMavenTasks(
          frameworkName = frameworkName,
          artifactVersion = version
        ) {
          !it.name.startsWith("macos") || includeMacos
        }
      }
    }
  }
}
