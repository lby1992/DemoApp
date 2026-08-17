package dev.dl.demoapp.feature.main

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavKey : NavKey {

}

fun EntryProviderScope<MainNavKey>.entryProviderBuilder() {

}