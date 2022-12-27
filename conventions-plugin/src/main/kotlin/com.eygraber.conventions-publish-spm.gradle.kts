import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.kotlin.kmp.spm.registerPublishSpmToMavenTasks
import com.eygraber.conventions.publishing.githubPackagesPublishing

plugins {
  `maven-publish`
}

val gitHubDefaults = gradleConventionsDefaultsService.github
val spmDefaults = gradleConventionsDefaultsService.spm

@Suppress("LabeledExpression")
with(gradleConventionsExtension) {
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
      "Please set owner in the gradleConventions github extension"
    }

    val repoName = requireNotNull(repoName) {
      "Please set repoName in the gradleConventions github extension"
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
          "Please set frameworkName in the gradleConventions spm extension"
        }

        val version = requireNotNull(version) {
          "Please set version in the gradleConventions spm extension"
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
