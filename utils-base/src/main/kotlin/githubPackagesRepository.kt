import com.eygraber.gradle.settings.GradleUtilsSettingsPlugin
import org.gradle.api.Action
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.getByName

public fun Settings.gitHubPackagesRepository(
  action: Action<GradleUtilsSettingsPlugin.Extension>
) {
  action.execute(
    extensions.getByName<GradleUtilsSettingsPlugin.Extension>(
      "gitHubPackagesRepository"
    )
  )
}
