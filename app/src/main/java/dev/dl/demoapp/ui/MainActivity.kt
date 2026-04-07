package dev.dl.demoapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.dl.demoapp.core.designsystem.theme.AppTheme
import dev.dl.demoapp.ui.app.AppContent
import dev.dl.demoapp.ui.app.rememberAppContentState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()

            AppTheme(
                darkTheme = uiState.useDarkTheme(isSystemDark),
                dynamicColor = uiState.dynamicColorTheming,
            ) {
                val appContentState = rememberAppContentState()

                AppContent(
                    appState = appContentState,
                )
            }
        }
    }
}