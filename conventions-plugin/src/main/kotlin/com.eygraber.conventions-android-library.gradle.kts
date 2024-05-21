@file:Suppress("UnstableApiUsage")

import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.kotlin.doOnFirstMatchingIncomingDependencyBeforeResolution
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  id("com.android.library")
  id("org.gradle.android.cache-fix")
}

val ext = gradleConventionsExtension
val androidDefaults = gradleConventionsDefaultsService.android
val kotlinDefaults = gradleConventionsDefaultsService.kotlin

ext.android.compileSdk = androidDefaults.compileSdk
ext.android.targetSdk = androidDefaults.targetSdk
ext.android.minSdk = androidDefaults.minSdk
ext.android.publishEverything = androidDefaults.publishEverything
ext.android.coreLibraryDesugaringDependency = androidDefaults.coreLibraryDesugaringDependency
ext.android.flavors = androidDefaults.flavors
ext.android.optInsToDependencyPredicate = androidDefaults.optInsToDependencyPredicate
ext.kotlin.jvmTargetVersion = kotlinDefaults.jvmTargetVersion

var isAndroidPublishingConfigured = false

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

  androidLibrary {
    compileSdk = androidCompileSdk

    val kmpManifestFilePath = "src/androidMain/AndroidManifest.xml"
    if(layout.projectDirectory.file(kmpManifestFilePath).asFile.exists()) {
      sourceSets.named("main") {
        manifest.srcFile(kmpManifestFilePath)
      }
    }

    val kmpResPath = "src/androidMain/res"
    if(layout.projectDirectory.file(kmpResPath).asFile.exists()) {
      sourceSets.named("main") {
        res.srcDir(kmpResPath)
      }
    }

    val kmpResourcesPath = "src/commonMain/resources"
    if(layout.projectDirectory.file(kmpResourcesPath).asFile.exists()) {
      sourceSets.named("main") {
        res.srcDir(kmpResourcesPath)
      }
    }

    defaultConfig {
      val consumerRulesProFile = layout.projectDirectory.file("consumer-rules.pro")
      if(consumerRulesProFile.asFile.exists()) {
        consumerProguardFile(consumerRulesProFile)
      }

      minSdk = androidMinSdk

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
      if(coreLibraryDesugaringDependency != null) {
        isCoreLibraryDesugaringEnabled = true
      }
    }

    packaging {
      resources.pickFirsts += "META-INF/*"
    }

    buildTypes {
      named("release") {
        isMinifyEnabled = false
      }
      named("debug") {
        isMinifyEnabled = false
      }
    }

    for((dimension, flavorsToRegister) in flavors) {
      if(dimension !in flavorDimensions) flavorDimensions += dimension

      for(flavor in flavorsToRegister) {
        // register throws if the name is already registered
        // and this block can be called multiple times
        runCatching {
          productFlavors.register(flavor.name)
        }
      }
    }

    testOptions {
      unitTests {
        isIncludeAndroidResources = true
      }
    }

    if(publishEverything) {
      // we can't configure this twice and awaitAndroidConfigured can be called twice (default and user config)
      // since publishing doesn't depend on values from the extension we can guard against this without issue
      if(!isAndroidPublishingConfigured) {
        publishing {
          multipleVariants {
            allVariants()
            withJavadocJar()
            withSourcesJar()
          }
        }
        isAndroidPublishingConfigured = true
      }
    }

    coreLibraryDesugaringDependency?.let { desugaringDependency ->
      dependencies {
        add("coreLibraryDesugaring", desugaringDependency)
      }
    }
  }

  androidLibraryComponents {
    val disabledFlavors = flavors.mapNotNull { (dimension, flavorsToRegister) ->
      val disabledFlavors = flavorsToRegister.filterNot { it.enabled }
      (dimension to disabledFlavors).takeIf { disabledFlavors.isNotEmpty() }
    }
    if(disabledFlavors.isNotEmpty()) {
      val selector = selector().let {
        var s = it
        for((dimension, disabled) in disabledFlavors) {
          for(disabledFlavor in disabled) {
            s = s.withFlavor(dimension to disabledFlavor.name)
          }
        }
        s
      }

      beforeVariants(selector) { variant ->
        variant.enable = false
      }
    }

    for((optIns, dependencyPredicate) in optInsToDependencyPredicate) {
      onVariants { variant ->
        doOnFirstMatchingIncomingDependencyBeforeResolution(
          configurationName = "${variant.name}RuntimeClasspath",
          dependencyPredicate = dependencyPredicate,
        ) {
          tasks.withType(KotlinCompilationTask::class.java).configureEach {
            compilerOptions {
              for(optIn in optIns) {
                freeCompilerArgs.addAll("-opt-in=${optIn.value}")
              }
            }
          }
        }
      }
    }
  }
}
