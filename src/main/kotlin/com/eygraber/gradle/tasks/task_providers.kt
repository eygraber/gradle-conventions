package com.eygraber.gradle.tasks

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal fun <T : Task> TaskProvider<out T>.dependsOn(taskName: String): TaskProvider<out T> {
  configure { it.dependsOn(taskName) }

  return this
}
