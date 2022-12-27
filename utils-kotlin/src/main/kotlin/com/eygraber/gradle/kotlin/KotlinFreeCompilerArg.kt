package com.eygraber.gradle.kotlin

public sealed class KotlinFreeCompilerArg(public val value: String) {
  public class Unknown(value: String) : KotlinFreeCompilerArg(value)
}
