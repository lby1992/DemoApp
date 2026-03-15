package dev.dl.demoapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.dl.demoapp.core.designsystem.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainActivityScreen(viewModel)
        }
    }
}

@Composable
private fun MainActivityScreen(viewModel: MainActivityViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()

    AppTheme(
        darkTheme = uiState.useDarkTheme(isSystemDark),
        dynamicColor = uiState.dynamicColorTheming,
    ) {
        // TODO emit app content
    }
}