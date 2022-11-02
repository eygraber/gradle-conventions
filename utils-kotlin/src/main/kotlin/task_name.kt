import java.util.Locale

@Suppress("Deprecation")
internal fun String.taskName(): String = capitalize(Locale.US)
