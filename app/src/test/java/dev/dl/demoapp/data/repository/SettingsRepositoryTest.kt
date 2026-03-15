package dev.dl.demoapp.data.repository

import dev.dl.demoapp.data.local.datastore.AppPreferencesDataSource
import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SettingsRepositoryTest {
    private lateinit var settingsRepository: SettingsRepository

    private val dataSource: AppPreferencesDataSource = mock()

    private val defaultLastOnboardingVersion = 1
    private val defaultDarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
    private val defaultUseDynamicColor = false

    @Before
    fun setUp() {
        val settings = AppSettings(
            lastOnboardingVersion = defaultLastOnboardingVersion,
            useDynamicColor = defaultUseDynamicColor,
            darkThemeConfig = defaultDarkThemeConfig,
        )
        // Without mockito-kotlin, you should do mocking like below:
//        Mockito.`when`(dataSource.appData)
//            .thenReturn(
//                flowOf(testSettings)
//            )

        // Stubbing Flow
        whenever(dataSource.appData)
            .thenReturn(
                flowOf(settings)
            )
        settingsRepository = SettingsRepository(dataSource)
    }

    @Test
    fun `settings emits value from datasource`() = runTest {
        val result = settingsRepository.settings.first()

        assertEquals(defaultLastOnboardingVersion, result.lastOnboardingVersion)
        assertEquals(defaultDarkThemeConfig, result.darkThemeConfig)
        assertEquals(defaultUseDynamicColor, result.useDynamicColor)
    }

    @Test
    fun `setLastOnboardingVersion delegates to datasource`() = runTest {
        settingsRepository.setLastOnboardingVersion(3)

        // Mockito sometimes struggles with suspend functions.
        // Using Mockito-Kotlin avoids most issues.
        verify(dataSource).setOnboardingShownVersion(3)
    }

    @Test
    fun `setDarkThemeConfig delegates to datasource`() = runTest {
        settingsRepository.setDarkThemeConfig(DarkThemeConfig.DARK)

        verify(dataSource).setDarkThemeConfig(DarkThemeConfig.DARK)
    }

    @Test
    fun `setDynamicColor delegates to datasource`() = runTest {
        settingsRepository.setDynamicColor(true)

        verify(dataSource).setDynamicColor(true)
    }
}