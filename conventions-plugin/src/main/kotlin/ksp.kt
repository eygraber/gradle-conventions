import com.eygraber.conventions.capitalize
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.kpm.external.ExternalVariantApi
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

interface KspDependencies {
  fun ksp(dependencyNotation: Any)
}

fun KotlinProjectExtension.configureKspSourceSets() {
  check(this !is KotlinMultiplatformExtension) {
    """
    |configureKspSourceSets isn't meant to be used with KMP.
    |Please use one of the functions meant for use with KMP:
    |  KotlinTarget.kspDependencies
    |  KotlinMultiplatformExtension.kspDependenciesForAllTargets
    |  KotlinMultiplatformExtension.commonMainKspDependencies
    """.trimMargin()
  }

  sourceSets.configureEach {
    kotlin.srcDir("build/generated/ksp/$name/kotlin")
    kotlin.srcDir("build/generated/ksp/$name/java")
    resources.srcDir("build/generated/ksp/$name/resources")
  }
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

  compilations.configureEach {
    kotlinSourceSets.forEach { sourceSet ->
      sourceSet.kotlin.srcDir("build/generated/ksp/$targetName/${sourceSet.name}/kotlin")
      sourceSet.resources.srcDir("build/generated/ksp/$targetName/${sourceSet.name}/resources")
    }
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

  sourceSets.named("commonMain").configure {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
  }

  project.tasks.withType(KotlinCompile::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
      dependsOn("kspCommonMainKotlinMetadata")
    }
  }
}
