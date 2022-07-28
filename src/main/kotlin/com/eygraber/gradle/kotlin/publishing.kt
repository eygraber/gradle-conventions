package com.eygraber.gradle.kotlin

import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign

/**
 * [https://youtrack.jetbrains.com/issue/KT-46466](https://youtrack.jetbrains.com/issue/KT-46466)
 */
public fun Project.fixPublishTaskImplicitDependencyOnSigningTasks() {
  val signingTasks = tasks.withType(Sign::class.java)
  tasks.withType(AbstractPublishToMaven::class.java).configureEach { publishTask ->
    publishTask.dependsOn(signingTasks)
  }
}
