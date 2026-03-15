package dev.dl.demoapp.ui

import dev.dl.demoapp.data.model.AppSettings
import dev.dl.demoapp.data.model.DarkThemeConfig
import dev.dl.demoapp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MainActivityViewModelTest {
    private val repository: SettingsRepository = mock()

    @Test
    fun `uiState starts with Loading`() = runTest {
        whenever(repository.settings)
            .thenReturn(emptyFlow())

        val viewModel = MainActivityViewModel(repository)

        assertEquals(MainActivityUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Loaded when settings emitted`() = runTest {
        val lastOnboardingVersion = 1
        val darkThemeConfig = DarkThemeConfig.DARK
        val useDynamicColor = false
        val settings = AppSettings(
            lastOnboardingVersion = lastOnboardingVersion,
            darkThemeConfig = darkThemeConfig,
            useDynamicColor = useDynamicColor,
        )

        whenever(repository.settings)
            .thenReturn(flowOf(settings))

        val viewModel = MainActivityViewModel(repository)
        val state = viewModel.uiState.first { it is MainActivityUiState.Loaded }

        assertTrue(state is MainActivityUiState.Loaded)
        assertTrue(state.useDarkTheme(false))
        assertFalse(state.dynamicColorTheming)
    }
}