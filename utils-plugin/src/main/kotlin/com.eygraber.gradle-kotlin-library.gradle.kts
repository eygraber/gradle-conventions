import com.eygraber.gradle.gradleUtilsDefaultsService
import com.eygraber.gradle.gradleUtilsExtension
import com.eygraber.gradle.kotlin.configureKgp

val kotlinDefaults = gradleUtilsDefaultsService.kotlin

with(gradleUtilsExtension) {
  with(kotlin) {
    jdkVersion = kotlinDefaults.jdkVersion
    jvmDistribution = kotlinDefaults.jvmDistribution
    allWarningsAsErrors = kotlinDefaults.allWarningsAsErrors
    explicitApiMode = kotlinDefaults.explicitApiMode
    configureJava = kotlinDefaults.configureJava
    useK2 = kotlinDefaults.useK2
    freeCompilerArgs = kotlinDefaults.freeCompilerArgs
    optIns = kotlinDefaults.optIns
  }

  awaitKotlinConfigured { isUserConfigured ->
    val jdkVersion = jdkVersion ?: if(isUserConfigured) null else ""

    if(jdkVersion != "") {
      configureKgp(
        jdkVersion = requireNotNull(jdkVersion) {
          "Please set jdkVersion in the gradleUtils kotlin extension"
        },
        jvmDistribution = jvmDistribution,
        allWarningsAsErrors = allWarningsAsErrors,
        explicitApiMode = explicitApiMode,
        configureJavaCompatibility = configureJava,
        freeCompilerArgs = freeCompilerArgs.toList(),
        optIns = optIns.toTypedArray()
      )
    }
  }
}
