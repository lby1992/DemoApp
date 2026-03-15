package dev.dl.demoapp.data.model

/**
 * Data model to hold the app settings.
 */
data class AppSettings(
    val lastOnboardingVersion: Int,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)
