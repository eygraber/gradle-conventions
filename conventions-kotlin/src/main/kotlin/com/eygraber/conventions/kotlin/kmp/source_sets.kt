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
 * Provides the existing [jsWasmMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jsWasmMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jsWasmMain")

/**
 * Provides the existing [jsWasmTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.jsWasmTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("jsWasmTest")

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

/**
 * Provides the existing [wasmMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmMain")

/**
 * Provides the existing [wasmTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.wasmTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("wasmTest")
