package dev.dl.demoapp.data.local.datastore

import dev.dl.demoapp.data.model.DarkThemeConfig
import dev.dl.demoapp.data.proto.AppPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppPreferencesDataSourceTest {

    private lateinit var dataSource: AppPreferencesDataSource

    @Before
    fun setup() {
        val fakeDataStore = FakeDataStore<AppPrefs>(AppPrefs.getDefaultInstance())
        dataSource = AppPreferencesDataSource(fakeDataStore)
    }

    @Test
    fun `last onboarding version is 0 by default`() = runTest {
        assertEquals(0, dataSource.appData.first().lastOnboardingVersion)
    }

    @Test
    fun `last onboarding version is correct when set`() = runTest {
        val expectedVersion = 10
        dataSource.setOnboardingShownVersion(expectedVersion)
        assertEquals(expectedVersion, dataSource.appData.first().lastOnboardingVersion)
    }

    @Test
    fun `dark theme config follows system by default`() = runTest {
        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, dataSource.appData.first().darkThemeConfig)
    }

    @Test
    fun `dark theme config is light when set`() = runTest {
        dataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)
        assertEquals(DarkThemeConfig.LIGHT, dataSource.appData.first().darkThemeConfig)
    }

    @Test
    fun `dark theme config is dark when set`() = runTest {
        dataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)
        assertEquals(DarkThemeConfig.LIGHT, dataSource.appData.first().darkThemeConfig)
    }

    @Test
    fun `dark theme config is dark when set to light then dark`() = runTest {
        dataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)
        dataSource.setDarkThemeConfig(DarkThemeConfig.DARK)
        assertEquals(DarkThemeConfig.DARK, dataSource.appData.first().darkThemeConfig)
    }

    @Test
    fun `don't use dynamic colors by default`() = runTest {
        assertFalse(dataSource.appData.first().useDynamicColor)
    }

    @Test
    fun `use dynamic colors when set`() = runTest {
        dataSource.setDynamicColor(true)
        assertTrue(dataSource.appData.first().useDynamicColor)
    }
}