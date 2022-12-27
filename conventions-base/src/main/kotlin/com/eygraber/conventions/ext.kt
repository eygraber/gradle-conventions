package com.eygraber.conventions

import java.util.Locale

@Suppress("Deprecation")
public fun String.capitalize(): String = capitalize(Locale.US)
