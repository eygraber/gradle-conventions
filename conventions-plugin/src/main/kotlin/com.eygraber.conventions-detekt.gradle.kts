import com.eygraber.conventions.detekt.configureDetekt
import com.eygraber.conventions.detekt.registerCommonTestDetektTask
import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import dev.detekt.gradle.extensions.DetektExtension

plugins {
  id("dev.detekt")
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

    configureDetekt(
      jvmTargetVersion = jvmTargetVersion,
    )

    extensions.configure<DetektExtension>("detekt") {
      val detektExt = gradleConventionsExtension.detekt
      this.autoCorrect.set(detektExt.autoCorrect)
      this.parallel.set(detektExt.parallel)
      this.debug.set(detektExt.debug)
      this.enableCompilerPlugin.set(detektExt.enableCompilerPlugin)
      this.buildUponDefaultConfig.set(detektExt.buildUponDefaultConfig)
    }

    dependencies {
      for(dependency in detektPluginDependencies) {
        add("detektPlugins", dependency)
      }
    }

    project.registerCommonTestDetektTask()
  }
}
