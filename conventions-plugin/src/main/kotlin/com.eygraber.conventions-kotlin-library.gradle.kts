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
    configureJava = kotlinDefaults.configureJava
    useK2 = kotlinDefaults.useK2
    freeCompilerArgs = kotlinDefaults.freeCompilerArgs
    optIns = kotlinDefaults.optIns
  }

  awaitKotlinConfigured {
    configureKgp(
      jvmTargetVersion = jvmTargetVersion,
      jdkToolchainVersion = jdkToolchainVersion,
      jvmDistribution = jvmDistribution,
      allWarningsAsErrors = allWarningsAsErrors,
      explicitApiMode = explicitApiMode,
      configureJavaCompatibility = configureJava,
      freeCompilerArgs = freeCompilerArgs.toList(),
      optIns = optIns.toTypedArray()
    )
  }
}
