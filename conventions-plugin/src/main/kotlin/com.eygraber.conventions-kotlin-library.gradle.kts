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
    useK2 = kotlinDefaults.useK2
    freeCompilerArgs = kotlinDefaults.freeCompilerArgs
    optIns = kotlinDefaults.optIns
  }

  val highestSupportedJava: JavaVersion = when {
    GradleVersion.current() < GradleVersion.version("8.3") -> JavaVersion.VERSION_17
    else -> JavaVersion.VERSION_20
  }

  val lowestSupportedJava: JavaVersion = JavaVersion.VERSION_17

  val buildJavaVersion: JavaVersion = when {
    JavaVersion.current() < lowestSupportedJava -> lowestSupportedJava
    JavaVersion.current() > highestSupportedJava -> highestSupportedJava
    else -> JavaVersion.current()
  }

  if(JavaVersion.current() != buildJavaVersion) {
    kotlin.jdkToolchainVersion = JavaLanguageVersion.of(buildJavaVersion.majorVersion.toInt())
  }

  awaitKotlinConfigured {
    configureKgp(
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
