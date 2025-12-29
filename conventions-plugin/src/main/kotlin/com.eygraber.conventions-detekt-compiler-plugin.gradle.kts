import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import dev.detekt.gradle.extensions.DetektExtension

plugins {
  id("dev.detekt.gradle.compiler-plugin")
}

val ext = gradleConventionsExtension
val detektDefaults = gradleConventionsDefaultsService.detekt
val kotlinDefaults = gradleConventionsDefaultsService.kotlin

ext.detekt.autoCorrect = detektDefaults.autoCorrect
ext.detekt.parallel = detektDefaults.parallel
ext.detekt.debug = detektDefaults.debug
ext.detekt.enableCompilerPlugin = detektDefaults.enableCompilerPlugin
ext.detekt.buildUponDefaultConfig = detektDefaults.buildUponDefaultConfig

ext.detekt.ignoredAndroidFlavors = detektDefaults.ignoredAndroidFlavors
ext.detekt.ignoredAndroidVariants = detektDefaults.ignoredAndroidVariants
ext.detekt.detektPluginDependencies = detektDefaults.detektPluginDependencies
ext.kotlin.jvmTargetVersion = kotlinDefaults.jvmTargetVersion

@Suppress("LabeledExpression")
ext.awaitKotlinConfigured { isKotlinUserConfigured ->
  ext.awaitDetektConfigured {
    if(jvmTargetVersion == null && !isKotlinUserConfigured) return@awaitDetektConfigured

    val d = this
    extensions.getByType(DetektExtension::class).apply {
      this.parallel.assign(d.parallel)
      this.debug = d.debug
      this.buildUponDefaultConfig = d.buildUponDefaultConfig

      val configFiles = files()

      val rootConfig = rootProject.file("detekt.yml")
      if(rootConfig.exists()) {
        configFiles.from(rootConfig)
      }

      val projectConfig = project.file("detekt.yml")
      if(projectConfig.exists()) {
        configFiles.from(projectConfig)
      }

      this.config.from(configFiles)
    }

    dependencies {
      for(dependency in detektPluginDependencies) {
        add("detektPlugins", dependency)
      }
    }
  }
}
