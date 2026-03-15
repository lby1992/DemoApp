package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.DataStore
import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig
import dev.dl.demoapp.data.proto.AppPrefs
import dev.dl.demoapp.data.proto.DarkThemeConfigProto
import dev.dl.demoapp.data.proto.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Data source to provide access to app preferences.
 */
class AppPreferencesDataSource @Inject constructor(
    private val appPrefs: DataStore<AppPrefs>
) {
    val appData: Flow<AppSettings> = appPrefs.data
        .map {
            AppSettings(
                lastOnboardingVersion = it.lastOnboardingVersion,
                useDynamicColor = it.useDynamicColor,
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
            )
        }

    /**
     * Update the latest version that has shown onboarding.
     */
    suspend fun setOnboardingShownVersion(versionCode: Int) {
        appPrefs.updateData {
            it.copy {
                this.lastOnboardingVersion = versionCode
            }
        }
    }

    /**
     * Update the dynamic color configuration.
     */
    suspend fun setDynamicColor(enabled: Boolean) {
        appPrefs.updateData {
            it.copy {
                this.useDynamicColor = enabled
            }
        }
    }

    /**
     * Update the dark theme configuration.
     */
    suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        appPrefs.updateData {
            it.copy {
                this.darkThemeConfig = when (config) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }
}