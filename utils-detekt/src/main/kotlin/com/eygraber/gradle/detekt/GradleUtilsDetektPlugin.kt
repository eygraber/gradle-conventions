package com.eygraber.gradle.detekt

import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class GradleUtilsDetektPlugin : Plugin<Project> {
  override fun apply(target: Project) {}
}
