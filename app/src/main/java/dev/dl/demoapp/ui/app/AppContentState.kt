package dev.dl.demoapp.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import dev.dl.demoapp.core.navigation.NavigationState
import dev.dl.demoapp.core.navigation.rememberNavigationState
import dev.dl.demoapp.feature.dashboard.DashboardNavKey

/**
 * Create and remember the [AppContentState].
 */
@Composable
fun rememberAppContentState(): AppContentState {
    val navigationState = rememberNavigationState(
        startKey = DashboardNavKey,
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys
    )
    return remember(navigationState) {
        AppContentState(
            navigationState = navigationState,
        )
    }
}

/**
 * State for the app content.
 *
 * @param navigationState The state of navigation.
 */
@Stable
class AppContentState(
    val navigationState: NavigationState,
)