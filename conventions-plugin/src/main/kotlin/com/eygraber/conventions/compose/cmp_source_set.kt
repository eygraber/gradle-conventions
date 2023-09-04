package com.eygraber.conventions.compose

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Provides the existing [cmpMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.cmpMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("cmpMain")

/**
 * Provides the existing [cmpTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.cmpTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("cmpTest")
