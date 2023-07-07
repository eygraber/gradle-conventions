import com.eygraber.conventions.capitalize
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.kpm.external.ExternalVariantApi
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

interface KspDependencies {
  fun ksp(dependencyNotation: Any)
}

fun KotlinTarget.kspDependencies(block: KspDependencies.() -> Unit) {
  val configurationName = "ksp${targetName.capitalize()}"
  project.dependencies {
    object : KspDependencies {
      override fun ksp(dependencyNotation: Any) {
        add(configurationName, dependencyNotation)
      }
    }.block()
  }
}

fun KotlinMultiplatformExtension.kspDependenciesForAllTargets(block: KspDependencies.() -> Unit) {
  targets.configureEach {
    if(targetName != "metadata") {
      kspDependencies(block)
    }
  }
}

@OptIn(ExternalVariantApi::class)
fun KotlinMultiplatformExtension.commonMainKspDependencies(block: KspDependencies.() -> Unit) {
  project.dependencies {
    object : KspDependencies {
      override fun ksp(dependencyNotation: Any) {
        add("kspCommonMainMetadata", dependencyNotation)
      }
    }.block()
  }

  project.plugins.withId("io.gitlab.arturbosch.detekt") {
    project.tasks.withType(Detekt::class.java).configureEach {
      if(name != "detekt") {
        dependsOn("kspCommonMainKotlinMetadata")
      }
    }
  }
}
