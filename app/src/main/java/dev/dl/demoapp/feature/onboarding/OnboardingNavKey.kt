package dev.dl.demoapp.feature.onboarding

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface OnboardingNavKey : NavKey {
    @Serializable
    data object Welcome : OnboardingNavKey

    @Serializable
    data object Guide : OnboardingNavKey
}