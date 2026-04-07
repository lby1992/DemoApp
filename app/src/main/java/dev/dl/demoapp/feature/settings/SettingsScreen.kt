package dev.dl.demoapp.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    SettingsScreenContent(modifier)
}

@Composable
private fun SettingsScreenContent(modifier: Modifier) {
    DummyScreen(
        "Settings",
    )
}