import com.eygraber.gradle.detekt.configureDetekt
import com.eygraber.gradle.detekt.registerCommonTestDetektTask
import com.eygraber.gradle.gradleUtilsDefaultsService
import com.eygraber.gradle.gradleUtilsExtension

plugins {
  id("io.gitlab.arturbosch.detekt")
}

val ext = gradleUtilsExtension
val detektDefaults = gradleUtilsDefaultsService.detekt
val kotlinDefaults = gradleUtilsDefaultsService.kotlin

ext.detekt.ignoredAndroidFlavors = detektDefaults.ignoredAndroidFlavors
ext.detekt.ignoredAndroidVariants = detektDefaults.ignoredAndroidVariants
ext.detekt.detektPluginDependencies = detektDefaults.detektPluginDependencies
ext.kotlin.jdkVersion = kotlinDefaults.jdkVersion

@Suppress("LabeledExpression")
ext.awaitKotlinConfigured { isKotlinUserConfigured ->
  ext.awaitDetektConfigured {
    if(jdkVersion == null && !isKotlinUserConfigured) return@awaitDetektConfigured

    configureDetekt(
      jdkVersion = requireNotNull(jdkVersion) {
        "Please set jdkVersion in the gradleUtils kotlin extension"
      }
    )

    dependencies {
      for(dependency in detektPluginDependencies) {
        add("detektPlugins", dependency)
      }
    }

    project.registerCommonTestDetektTask()
  }
}
