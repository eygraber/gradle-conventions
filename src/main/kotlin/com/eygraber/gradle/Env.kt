package com.eygraber.gradle

public object Env {
  public val isGitHubActions: Boolean = System.getenv("CI") == "true"
  public val isCI: Boolean = isGitHubActions
}
