package com.eygraber.gradle.tasks

import org.gradle.api.Project

public fun Project.deleteRootBuildDirWhenCleaning() {
  require(project === rootProject) {
    "deleteRootBuildDirWhenCleaning should only be called from the root project"
  }

  requireNotNull(plugins.findPlugin("base")) {
    "The \"base\" plugin must be applied to the root project in order to call deleteRootBuildDirWhenCleaning"
  }

  val buildDir = buildDir

  tasks.named("clean").configure {
    it.doFirst {
      delete(buildDir)
    }
  }
}
