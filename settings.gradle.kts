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
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      content {
        includeGroupByRegex("org\\.jetbrains\\.compose.*")
      }
    }
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
  id("com.gradle.enterprise") version "3.12.2"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("conventions-base")
include("conventions-detekt")
include("conventions-kotlin")
include("conventions-plugin")

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    if(System.getenv("CI") != null) {
      termsOfServiceAgree = "yes"
      publishAlways()
    }
  }
}
