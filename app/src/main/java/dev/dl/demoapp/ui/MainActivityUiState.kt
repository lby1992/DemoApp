package dev.dl.demoapp.ui

import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Loaded(
        private val appSettings: AppSettings
    ) : MainActivityUiState {
        override val dynamicColorTheming = appSettings.useDynamicColor

        // Just check if it'd been shown after installation.
        val showOnboarding = appSettings.lastOnboardingVersion > 0

        override fun useDarkTheme(isSystemDarkTheme: Boolean) =
            when (appSettings.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

    val shouldKeepSplashScreen: Boolean
        get() = this is Loading

    val dynamicColorTheming: Boolean
        get() = false

    fun useDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}