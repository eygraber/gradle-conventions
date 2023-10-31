package com.eygraber.conventions.kotlin.kmp

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

public val Project.kmpSourceSets: NamedDomainObjectContainer<KotlinSourceSet>
  get() = extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets

/**
 * Provides the existing [androidTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.androidTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("androidTest")

/**
 * Provides the existing [androidUnitTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.androidUnitTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("androidUnitTest")

/**
 * Provides the existing [androidInstrumentedTest][KotlinSourceSet] element.
 */
@Suppress("ktlint:standard:max-line-length")
public val NamedDomainObjectContainer<KotlinSourceSet>.androidInstrumentedTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("androidInstrumentedTest")

/**
 * Provides the existing [wasmJsMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmJsMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmJsMain")

/**
 * Provides the existing [wasmJsTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmJsTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmJsTest")

/**
 * Provides the existing [wasmWasiMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmWasiMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmWasiMain")

/**
 * Provides the existing [wasmWasiTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmWasiTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmWasiTest")
