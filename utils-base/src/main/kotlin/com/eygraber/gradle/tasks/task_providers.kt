package com.eygraber.gradle.tasks

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

public fun <T : Task> TaskProvider<out T>.dependsOn(taskName: String): TaskProvider<out T> {
  configure { dependsOn(taskName) }

  return this
}
