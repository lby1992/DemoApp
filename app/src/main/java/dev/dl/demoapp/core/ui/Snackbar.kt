package dev.dl.demoapp.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * A [CompositionLocal] that provides a [SnackbarHostState] to the composables in the app.
 * This allows us to show snackbars from anywhere in the app without having to pass the [SnackbarHostState] down the composable hierarchy.
 */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided.")
}