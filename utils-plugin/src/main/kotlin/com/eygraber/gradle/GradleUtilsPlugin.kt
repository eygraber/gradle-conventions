package com.eygraber.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class GradleUtilsPlugin : Plugin<Project> {
  override fun apply(target: Project) {}
}
