package com.eygraber.conventions.kotlin.kmp

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

public val Project.kmpSourceSets: NamedDomainObjectContainer<KotlinSourceSet>
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets
