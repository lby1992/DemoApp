package dev.dl.demoapp.data.repository

import dev.dl.demoapp.data.local.datastore.AppPreferencesDataSource
import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository that manages application settings.
 *
 * This repository exposes a [Flow] of [AppSettings] and provides APIs
 * for updating user preferences such as onboarding state and UI theme configuration.
 */
class SettingsRepository @Inject constructor(
    private val appPrefsDataSource: AppPreferencesDataSource
) {
    /**
     * Stream of current application settings.
     */
    val settings: Flow<AppSettings> = appPrefsDataSource.appData

    /**
     * Persists the last completed onboarding version.
     */
    suspend fun setLastOnboardingVersion(version: Int) {
        appPrefsDataSource.setOnboardingShownVersion(version)
    }

    /**
     * Updates the dark theme configuration.
     */
    suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        appPrefsDataSource.setDarkThemeConfig(config)
    }

    /**
     * Enables or disables dynamic color support.
     */
    suspend fun setDynamicColor(enabled: Boolean) {
        appPrefsDataSource.setDynamicColor(enabled)
    }
}