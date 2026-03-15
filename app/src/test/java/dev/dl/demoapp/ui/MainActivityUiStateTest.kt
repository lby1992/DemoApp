package dev.dl.demoapp.ui

import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainActivityUiStateTest {

    @Test
    fun `useDarkTheme returns system value when follow system`() {
        val state = MainActivityUiState.Loaded(
            AppSettings(
                lastOnboardingVersion = 0,
                darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                useDynamicColor = false,
            )
        )

        assertTrue(state.useDarkTheme(true))
        assertFalse(state.useDarkTheme(false))
    }

    @Test
    fun `useDarkTheme returns true when dark mode forced`() {
        val state = MainActivityUiState.Loaded(
            AppSettings(
                lastOnboardingVersion = 0,
                darkThemeConfig = DarkThemeConfig.DARK,
                useDynamicColor = false,
            )
        )

        assertTrue(state.useDarkTheme(false))
    }
}