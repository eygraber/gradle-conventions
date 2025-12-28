import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.project.common.JvmConventionDependencyHandler
import com.eygraber.conventions.project.common.KmpSourceSetConventionDependencyHandler
import com.eygraber.conventions.project.common.KmpTopLevelConventionDependencyHandler
import com.eygraber.conventions.project.common.ResolutionVersionSelector
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val ext = gradleConventionsExtension
val dependenciesDefaults = gradleConventionsDefaultsService.projectCommon
ext.projectCommon.resolutionVersionSelector = dependenciesDefaults.resolutionVersionSelector
ext.projectCommon.kmpSourceSetProjectDependencies = dependenciesDefaults.kmpSourceSetProjectDependencies
ext.projectCommon.projectDependencies = dependenciesDefaults.projectDependencies

ext.awaitProjectCommonConfigured {
  // com.github.ben-manes.versions sets the version of dependencies
  // to + in order to find the latest versions and we don't want to mess with that
  if("dependencyUpdates" !in gradle.startParameter.taskNames) {
    if(resolutionVersionSelector != null) {
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
                        "Version must be either a String or a Provider<String> (was ${version.javaClass.canonicalName})",
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
                  override val useTarget = { target: Any ->
                    useTarget(
                      when(target) {
                        is String -> target
                        is Provider<*> -> when(val providedTarget = target.get()) {
                          is String -> providedTarget
                          is ModuleVersionSelector -> providedTarget
                          else -> error(
                            "Target notation must be provided as a String or a ModuleVersionSelector (was ${version.javaClass.canonicalName})",
                          )
                        }
                        is Map<*, *> -> target
                        else -> error(
                          "Target notation must be a Map, String or a ModuleVersionSelector (was ${version.javaClass.canonicalName})",
                        )
                      },
                    )
                  }
                },
              )
            }
          }
        }
      }
    }
  }

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
      val kmpProject = project
      if(projectDependencies.isNotEmpty()) {
        dependencies {
          val handler = KmpTopLevelConventionDependencyHandler(this, kmpProject)
          projectDependencies.forEach { block ->
            block(handler)
          }
        }
      }

      @Suppress("UnusedVariable")
      if(kmpSourceSetProjectDependencies.isNotEmpty()) {
        sourceSets.configureEach {
          kmpSourceSetProjectDependencies.forEach { (predicate, block) ->
            if(predicate(this)) {
              dependencies {
                val handler = KmpSourceSetConventionDependencyHandler(this, kmpProject)
                block(handler)
              }
            }
          }
        }
      }
    }
  }

  plugins.withId("org.jetbrains.kotlin.jvm") {
    if(projectDependencies.isNotEmpty()) {
      dependencies {
        val handler = JvmConventionDependencyHandler(this, project)
        projectDependencies.forEach { block ->
          block(handler)
        }
      }
    }
  }

  plugins.withId("org.jetbrains.kotlin.android") {
    if(projectDependencies.isNotEmpty()) {
      dependencies {
        val handler = JvmConventionDependencyHandler(this, project)
        projectDependencies.forEach { block ->
          block(handler)
        }
      }
    }
  }
}
