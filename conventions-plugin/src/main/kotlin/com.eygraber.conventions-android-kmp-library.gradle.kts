import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension

plugins {
  id("com.android.kotlin.multiplatform.library")
  id("org.gradle.android.cache-fix")
}

val ext = gradleConventionsExtension
val androidDefaults = gradleConventionsDefaultsService.android
val kotlinDefaults = gradleConventionsDefaultsService.kotlin

ext.android.compileSdk = androidDefaults.compileSdk
ext.android.targetSdk = androidDefaults.targetSdk
ext.android.minSdk = androidDefaults.minSdk
ext.android.doNotRunLintWhenRunningReleaseBuildTasks = androidDefaults.doNotRunLintWhenRunningReleaseBuildTasks
ext.android.coreLibraryDesugaringDependency = androidDefaults.coreLibraryDesugaringDependency

@Suppress("LabeledExpression")
ext.awaitAndroidConfigured { isAndroidUserConfigured ->
  val isSdkVersionsConfigured = compileSdk > 0 && minSdk > 0
  if(!isSdkVersionsConfigured && !isAndroidUserConfigured) return@awaitAndroidConfigured

  check(compileSdk > 0) {
    "android.compileSdk doesn't have a value set"
  }

  check(minSdk > 0) {
    "android.minSdk doesn't have a value set"
  }

  val androidCompileSdk = compileSdk
  val androidMinSdk = minSdk

  androidKmpLibrary {
    compileSdk = androidCompileSdk
    minSdk = androidMinSdk

    @Suppress("UnstableApiUsage")
    optimization {
      val consumerRulesProFile = layout.projectDirectory.file("consumer-rules.pro").asFile
      if(consumerRulesProFile.exists()) {
        consumerKeepRules.files += consumerRulesProFile
      }
    }

    if(coreLibraryDesugaringDependency != null) {
      enableCoreLibraryDesugaring = true
    }

    packaging {
      resources.pickFirsts += "META-INF/*"
    }

    if(doNotRunLintWhenRunningReleaseBuildTasks == true) {
      lint {
        checkReleaseBuilds = false
      }
    }

    coreLibraryDesugaringDependency?.let { desugaringDependency ->
      dependencies.add("coreLibraryDesugaring", desugaringDependency)
    }
  }
}
