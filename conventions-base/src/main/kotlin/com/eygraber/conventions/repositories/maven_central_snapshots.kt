package com.eygraber.conventions.repositories

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.Actions

@JvmOverloads
public fun RepositoryHandler.mavenCentralSnapshots(
  action: Action<MavenArtifactRepository> = Actions.doNothing(),
) {
  maven {
    setUrl("https://oss.sonatype.org/content/repositories/snapshots")

    mavenContent {
      snapshotsOnly()
    }

    action.execute(this)
  }
}

@JvmOverloads
public fun RepositoryHandler.mavenCentralSnapshotsS01(
  action: Action<MavenArtifactRepository> = Actions.doNothing(),
) {
  maven {
    setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots")

    mavenContent {
      snapshotsOnly()
    }

    action.execute(this)
  }
}

/**
 * see [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle)
 */
@JvmOverloads
public fun RepositoryHandler.mavenCentralPortalSnapshots(
  action: Action<MavenArtifactRepository> = Actions.doNothing(),
) {
  maven {
    name = "Central Portal Snapshots"
    setUrl("https://central.sonatype.com/repository/maven-snapshots/")

    mavenContent {
      snapshotsOnly()
    }

    action.execute(this)
  }
}
