import com.eygraber.conventions.dependencies.ConventionDependencyHandler
import com.eygraber.conventions.dependencies.ResolutionVersionSelector
import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension

val ext = gradleConventionsExtension
val dependenciesDefaults = gradleConventionsDefaultsService.dependencies
ext.projectDependencies.resolutionVersionSelector = dependenciesDefaults.resolutionVersionSelector
ext.projectDependencies.projectDependencies = dependenciesDefaults.projectDependencies

ext.awaitProjectDependenciesConfigured {
  // com.github.ben-manes.versions sets the version of dependencies
  // to + in order to find the latest versions and we don't want to mess with that
  if("dependencyUpdates" !in gradle.startParameter.taskNames) {
    configurations.configureEach {
      val configurationName = name

      resolutionStrategy {
        eachDependency {
          resolutionVersionSelector?.let { selector ->
            requested.selector(
              object : ResolutionVersionSelector {
                override val configurationName = configurationName
                override val useVersion = { version: Any ->
                  val versionError = {
                    error(
                      "Version must be either a String or a Provider<String> (was ${version.javaClass.canonicalName})"
                    )
                  }
                  when(version) {
                    is String -> useVersion(version)
                    is Provider<*> -> when(val versionString = version.get()) {
                      is String -> useVersion(versionString)
                      else -> versionError()
                    }

                    else -> versionError()
                  }
                }
              }
            )
          }
        }
      }
    }
  }

  dependencies {
    val handler = ConventionDependencyHandler(this, project)
    projectDependencies.forEach { block ->
      block(handler)
    }
  }
}
