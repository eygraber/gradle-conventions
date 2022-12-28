package com.eygraber.conventions.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

public abstract class GradleConventionsSettingsPlugin : Plugin<Settings> {
  override fun apply(target: Settings) {}
}
