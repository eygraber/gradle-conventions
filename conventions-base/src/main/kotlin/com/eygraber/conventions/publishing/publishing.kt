package com.eygraber.conventions.publishing

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

public fun Project.configurePublishingRepositories(action: Action<ArtifactRepository>) {
  plugins.withId("maven-publish") {
    extensions.getByType<PublishingExtension>().repositories.configureEach(action)
  }
}
