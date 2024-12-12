pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    gradlePluginPortal()
    mavenCentral()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "gradle-conventions"

plugins {
  id("com.gradle.develocity") version "3.19"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("conventions-base")
include("conventions-detekt")
include("conventions-kotlin")
include("conventions-plugin")

develocity {
  val isCI = System.getenv("CI") != null
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    publishing.onlyIf { isCI }
    if(isCI) {
      termsOfUseAgree = "yes"
    }
  }
}
