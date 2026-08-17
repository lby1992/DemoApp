package dev.dl.demoapp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dl.demoapp.data.repository.SettingsRepository
import dev.dl.demoapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val state: StateFlow<AppState> =
        combine(
            settingsRepository.settings
                .distinctUntilChanged { old, new ->
                    old.lastOnboardingVersion != new.lastOnboardingVersion
                }
                .map { it.lastOnboardingVersion == 0 },
            authRepository.isLoggedIn,
        ) { shouldShowOnboarding, isLoggedIn ->
            if (shouldShowOnboarding) {
                AppState.Onboarding
            } else if (!isLoggedIn) {
                AppState.Auth
            } else {
                AppState.Main
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                initialValue = AppState.Loading,
            )

    fun dispatch(action: AppAction) {
        when (action) {
            AppAction.Initialize -> TODO()
            AppAction.OnboardingFinished -> {
                viewModelScope.launch {
                    settingsRepository.setLastOnboardingVersion(0) // TODO
                }
            }

            AppAction.LoginSuccess -> TODO()
            AppAction.Logout -> TODO()
        }
    }

    private inline fun reduce(block: () -> AppState) {

    }
}