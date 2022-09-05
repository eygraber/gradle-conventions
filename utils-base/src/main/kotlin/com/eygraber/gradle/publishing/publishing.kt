package com.eygraber.gradle.publishing

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.kotlin.dsl.getByType

public fun Project.configurePublishingRepositories(action: Action<ArtifactRepository>) {
  plugins.whenPluginAdded {
    if(this::class.java == PublishingPlugin::class.java) {
      extensions.getByType<PublishingExtension>().repositories.configureEach(action)
    }
  }
}
