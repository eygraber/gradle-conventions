@file:Suppress("MissingPackageDeclaration")

import com.eygraber.conventions.capitalize
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import dev.detekt.gradle.Detekt as Detekt2

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

fun KotlinMultiplatformExtension.commonMainKspDependencies(
  project: Project,
  block: KspDependencies.() -> Unit,
) {
  project.dependencies {
    object : KspDependencies {
      override fun ksp(dependencyNotation: Any) {
        add("kspCommonMainMetadata", dependencyNotation)
      }
    }.block()
  }

  sourceSets.named("commonMain").configure {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
  }

  project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
      dependsOn("kspCommonMainKotlinMetadata")
    }
  }

  project.plugins.withId("io.gitlab.arturbosch.detekt") {
    project.tasks.withType(Detekt::class.java).configureEach {
      if(name != "detekt") {
        dependsOn("kspCommonMainKotlinMetadata")
      }
    }
  }

  project.plugins.withId("dev.detekt") {
    project.tasks.withType(Detekt2::class.java).configureEach {
      if(name != "detekt") {
        dependsOn("kspCommonMainKotlinMetadata")
      }
    }
  }
}
