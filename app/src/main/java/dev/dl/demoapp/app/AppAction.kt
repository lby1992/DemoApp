package dev.dl.demoapp.app

sealed interface AppAction {
    data object Initialize : AppAction
    data object OnboardingFinished : AppAction
    data object LoginSuccess : AppAction
    data object Logout : AppAction
}