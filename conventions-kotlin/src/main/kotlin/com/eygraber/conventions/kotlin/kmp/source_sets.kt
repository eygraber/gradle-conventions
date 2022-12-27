package com.eygraber.conventions.kotlin.kmp

import com.eygraber.conventions.publishing.configurePublishingRepositories
import com.eygraber.conventions.tasks.dependsOn
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import taskName

public val Project.kmpSourceSets: NamedDomainObjectContainer<KotlinSourceSet>
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets

public fun KotlinTarget.mainAndTestSourceSets(): Pair<KotlinSourceSet, KotlinSourceSet> =
  compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME).defaultSourceSet to
    compilations.getByName(KotlinCompilation.TEST_COMPILATION_NAME).defaultSourceSet

public fun KotlinMultiplatformExtension.createSharedSourceSet(
  project: Project,
  name: String,
  enableTasks: Boolean = true
) {
  with(sourceSets) {
    create("${name}Main") {
      dependsOn(getByName("commonMain"))
    }

    create("${name}Test") {
      dependsOn(getByName("commonTest"))
    }
  }

  val sourceSetNameForTasks = name.taskName()

  project.configurePublishingRepositories {
    val repoNameForTasks = this.name.taskName()
    project.tasks.register(
      "publish${sourceSetNameForTasks}PublicationTo${repoNameForTasks}Repository"
    ) {
      group = "Publishing"
      description =
        "Publishes all Maven 'apple' publications produced by this project to the githubPackages repository."
      enabled = enableTasks
    }
  }
}

public fun <T : KotlinTarget> KotlinMultiplatformExtension.createNestedSharedSourceSetForTargets(
  project: Project,
  name: String,
  targets: List<T>,
  parentSourceSetName: String,
  createIntermediatePublishingTasks: Boolean = true,
  enableTasks: Boolean = true,
  configureTarget: (T) -> Unit
) {
  val (nestedMainSourceSet, nestedTestSourceSet) = with(sourceSets) {
    val main = create("${name}Main") {
      dependsOn(getByName("${parentSourceSetName}Main"))
    }

    val test = create("${name}Test") {
      dependsOn(getByName("${parentSourceSetName}Test"))
    }

    main to test
  }

  val sourceSetNameForTasks = name.taskName()
  val parentSourceSetNameForTasks = parentSourceSetName.taskName()

  if(createIntermediatePublishingTasks) {
    project.configurePublishingRepositories {
      val repoNameForTasks = this.name.taskName()
      val publishTask = project.tasks.register(
        "publish${sourceSetNameForTasks}PublicationTo${repoNameForTasks}Repository"
      ) {
        group = "Publishing"
        description =
          "Publishes all Maven '$name' publications produced by this project to the githubPackages repository."
        enabled = enableTasks
      }

      project.tasks.named(
        "publish${parentSourceSetNameForTasks}PublicationTo${repoNameForTasks}Repository"
      ).dependsOn(publishTask.name)
    }
  }

  targets.forEach { target ->
    configureTarget(target)

    val targetNameForTasks = target.name.taskName()

    val (targetMainSourceSet, targetTestSourceSet) = target.mainAndTestSourceSets()
    targetMainSourceSet.dependsOn(nestedMainSourceSet)
    targetTestSourceSet.dependsOn(nestedTestSourceSet)

    if(createIntermediatePublishingTasks) {
      project.configurePublishingRepositories {
        val repoNameForTasks = this.name.taskName()

        project.tasks.named(
          "publish${sourceSetNameForTasks}PublicationTo${repoNameForTasks}Repository"
        ).dependsOn(
          "publish${targetNameForTasks}PublicationTo${repoNameForTasks}Repository"
        )
      }
    }
  }
}

/**
 * Provides the existing [androidMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.androidMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("androidMain")

/**
 * Provides the existing [androidTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.androidTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("androidTest")

/**
 * Provides the existing [appleMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.appleMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("appleMain")

/**
 * Provides the existing [appleTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.appleTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("appleTest")

/**
 * Provides the existing [iosMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.iosMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("iosMain")

/**
 * Provides the existing [iosTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.iosTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("iosTest")

/**
 * Provides the existing [macosMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.macosMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("macosMain")

/**
 * Provides the existing [macosTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.macosTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("macosTest")

/**
 * Provides the existing [jsMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jsMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jsMain")

/**
 * Provides the existing [jsTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jsTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jsTest")

/**
 * Provides the existing [jvmMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jvmMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jvmMain")

/**
 * Provides the existing [jvmTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jvmTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jvmTest")
