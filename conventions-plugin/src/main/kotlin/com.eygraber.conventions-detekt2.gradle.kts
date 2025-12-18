import com.eygraber.conventions.detekt.configureDetekt2
import com.eygraber.conventions.detekt.registerCommonTestDetekt2Task
import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import org.gradle.kotlin.dsl.dependencies

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

    configureDetekt2(
      jvmTargetVersion = jvmTargetVersion,
      autoCorrect = autoCorrect,
      parallel = parallel,
      debug = debug,
      enableCompilerPlugin = enableCompilerPlugin,
      buildUponDefaultConfig = buildUponDefaultConfig
    )

    dependencies {
      for(dependency in detektPluginDependencies) {
        add("detektPlugins", dependency)
      }
    }

    project.registerCommonTestDetekt2Task()
  }
}
