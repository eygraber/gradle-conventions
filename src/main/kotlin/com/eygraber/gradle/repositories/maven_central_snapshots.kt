package com.eygraber.gradle.repositories

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.Actions

@JvmOverloads
public fun RepositoryHandler.mavenCentralSnapshots(
  action: Action<MavenArtifactRepository> = Actions.doNothing()
) {
  maven { maven ->
    maven.setUrl("https://oss.sonatype.org/content/repositories/snapshots")

    action.execute(maven)
  }
}

@JvmOverloads
public fun RepositoryHandler.mavenCentralSnapshotsS01(
  action: Action<MavenArtifactRepository> = Actions.doNothing()
) {
  maven { maven ->
    maven.setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots")

    action.execute(maven)
  }
}
