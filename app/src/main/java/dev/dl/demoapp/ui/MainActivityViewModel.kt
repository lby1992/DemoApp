package dev.dl.demoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dl.demoapp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for [MainActivity].
 *
 * Observes application settings from [SettingsRepository] and exposes them
 * as a UI-friendly state for the activity.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * UI state derived from application settings.
     */
    val uiState: StateFlow<MainActivityUiState> = settingsRepository.settings
        .map { MainActivityUiState.Loaded(it) }
        .stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
        )
}

private const val STOP_TIMEOUT = 5_000L