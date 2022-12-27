import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.kotlin.configureKgp

val kotlinDefaults = gradleConventionsDefaultsService.kotlin

with(gradleConventionsExtension) {
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
          "Please set jdkVersion in the gradleConventions kotlin extension"
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
