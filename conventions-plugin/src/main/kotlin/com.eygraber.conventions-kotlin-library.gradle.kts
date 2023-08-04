import com.android.build.api.dsl.CompileOptions
import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.kotlin.configureKgp

val kotlinDefaults = gradleConventionsDefaultsService.kotlin

with(gradleConventionsExtension) {
  with(kotlin) {
    jvmTargetVersion = kotlinDefaults.jvmTargetVersion
    jdkToolchainVersion = kotlinDefaults.jdkToolchainVersion
    jvmDistribution = kotlinDefaults.jvmDistribution
    allWarningsAsErrors = kotlinDefaults.allWarningsAsErrors
    explicitApiMode = kotlinDefaults.explicitApiMode
    configureJavaTargetVersion = kotlinDefaults.configureJavaTargetVersion
    languageVersion = kotlinDefaults.languageVersion
    apiVersion = kotlinDefaults.apiVersion
    isProgressiveModeEnabled = kotlinDefaults.isProgressiveModeEnabled
    freeCompilerArgs = kotlinDefaults.freeCompilerArgs
    optIns = kotlinDefaults.optIns
  }

  awaitKotlinConfigured {
    val buildJavaVersion = configureKgp(
      jvmTargetVersion = jvmTargetVersion,
      jdkToolchainVersion = jdkToolchainVersion,
      jvmDistribution = jvmDistribution,
      allWarningsAsErrors = allWarningsAsErrors,
      explicitApiMode = explicitApiMode,
      configureJavaTargetVersion = configureJavaTargetVersion,
      freeCompilerArgs = freeCompilerArgs.toList(),
      optIns = optIns.toTypedArray()
    )

    fun CompileOptions.configureJvmTarget() {
      val jvmTargetVersion = jvmTargetVersion
      if(configureJavaTargetVersion) {
        val androidJvmTargetVersion = jvmTargetVersion?.let {
          JavaVersion.toVersion(it.target)
        } ?: buildJavaVersion

        sourceCompatibility = androidJvmTargetVersion
        targetCompatibility = androidJvmTargetVersion
      }
    }

    plugins.withId("com.android.application") {
      androidApp {
        compileOptions.configureJvmTarget()
      }
    }

    plugins.withId("com.android.library") {
      androidLibrary {
        compileOptions.configureJvmTarget()
      }
    }
  }
}
