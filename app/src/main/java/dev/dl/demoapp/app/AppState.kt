package dev.dl.demoapp.app

sealed interface AppState {
    data object Loading : AppState
    data object Onboarding : AppState
    data object Auth : AppState
    data object Main : AppState
}