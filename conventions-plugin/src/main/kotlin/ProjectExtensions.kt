@file:Suppress("NOTHING_TO_INLINE", "MissingPackageDeclaration")

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal inline fun Project.androidApp(action: Action<ApplicationExtension>) {
  action.execute(extensions.getByType(ApplicationExtension::class.java))
}

internal inline fun Project.androidLibraryComponents(action: Action<LibraryAndroidComponentsExtension>) {
  action.execute(extensions.getByType(LibraryAndroidComponentsExtension::class.java))
}

internal inline fun Project.androidLibrary(action: Action<LibraryExtension>) {
  action.execute(extensions.getByType(LibraryExtension::class.java))
}

internal inline fun Project.androidKmpLibrary(action: Action<KotlinMultiplatformAndroidLibraryExtension>) {
  action.execute(extensions.getByType(KotlinMultiplatformAndroidLibraryExtension::class.java))
}

internal val Project.compose: ComposeExtension
  get() =
    (this as ExtensionAware).extensions.getByName("compose") as ComposeExtension

internal val Project.kotlin: KotlinProjectExtension
  get() =
    (this as ExtensionAware).extensions.getByName("kotlin") as KotlinProjectExtension

internal inline fun Project.publishing(action: Action<PublishingExtension>) {
  action.execute(extensions.getByType(PublishingExtension::class.java))
}

internal inline fun Project.mavenPublishing(action: Action<MavenPublishBaseExtension>) {
  action.execute(extensions.getByType(MavenPublishBaseExtension::class.java))
}

internal fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
  add("implementation", dependencyNotation)
