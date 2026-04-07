package dev.dl.demoapp.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsNavKey : NavKey

fun EntryProviderScope<NavKey>.settingsEntry(navigator: Navigator) = entry<SettingsNavKey> {
    SettingsScreen()
}