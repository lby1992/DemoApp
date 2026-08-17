package dev.dl.demoapp.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.dl.demoapp.feature.auth.AuthFlow
import dev.dl.demoapp.feature.main.MainFlow
import dev.dl.demoapp.feature.onboarding.OnboardingFlow

@Composable
fun AppRoot(
    viewModel: AppViewModel = hiltViewModel(),
) {
    val appState by viewModel.state.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = appState,
        modifier = Modifier.fillMaxSize(),
        label = "AppRootTransition",
        transitionSpec = {
            when (initialState) {
                // Onboarding → Auth
                is AppState.Onboarding if targetState is AppState.Auth -> {
                    fadeIn() togetherWith fadeOut()
                }

                // Auth → Main
                is AppState.Auth if targetState is AppState.Main -> {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                }

                // Main → Auth（logout）
                is AppState.Main if targetState is AppState.Auth -> {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }

                else -> {
                    fadeIn() togetherWith fadeOut()
                }
            }
        },
    ) { targetState ->
        when (targetState) {
            AppState.Loading -> Unit // Splash screen is delegated by Splash lib.
            AppState.Auth -> AuthFlow()
            AppState.Main -> MainFlow()
            AppState.Onboarding -> OnboardingFlow(
                onFinished = {
                    //TODO
                }
            )
        }
    }
}