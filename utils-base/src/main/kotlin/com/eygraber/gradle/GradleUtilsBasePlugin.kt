package com.eygraber.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class GradleUtilsBasePlugin : Plugin<Project> {
  override fun apply(target: Project) {}
}
