import com.eygraber.conventions.detekt.configureDetekt
import com.eygraber.conventions.detekt.registerCommonTestDetektTask
import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension

plugins {
  id("io.gitlab.arturbosch.detekt")
}

val ext = gradleConventionsExtension
val detektDefaults = gradleConventionsDefaultsService.detekt
val kotlinDefaults = gradleConventionsDefaultsService.kotlin

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
        "Please set jdkVersion in the gradleConventions kotlin extension"
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
