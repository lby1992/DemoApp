package dev.dl.demoapp.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.dl.demoapp.core.navigation.Navigator
import dev.dl.demoapp.core.navigation.toEntries
import dev.dl.demoapp.core.ui.LocalSnackbarHostState
import dev.dl.demoapp.feature.dashboard.dashboardEntry
import dev.dl.demoapp.feature.expense.detail.expenseDetailEntry
import dev.dl.demoapp.feature.expense.expenseEntry
import dev.dl.demoapp.feature.settings.settingsEntry
import dev.dl.demoapp.feature.tasks.detail.taskDetailEntry
import dev.dl.demoapp.feature.tasks.todosEntry

@Composable
fun AppContent(
    appState: AppContentState,
    modifier: Modifier = Modifier
) {
    val navigator = remember { Navigator(appState.navigationState) }
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    )
    {
        Scaffold(
            modifier = modifier,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    hostState = LocalSnackbarHostState.current,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.exclude(
                            WindowInsets.ime,
                        ),
                    ),
                )
            },
            bottomBar = {
                NavigationBar {
                    TOP_LEVEL_NAV_ITEMS.forEach { (key, item) ->
                        val selected = appState.navigationState.currentTopLevelKey == key
                        val title = stringResource(item.titleTextId)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigator.navigateTo(key, true) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = title,
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                        )
                    }
                }
            },
        ) { paddingValues ->
            val entryProvider = entryProvider {
                dashboardEntry(navigator)
                todosEntry(navigator)
                taskDetailEntry(navigator)
                expenseEntry(navigator)
                expenseDetailEntry(navigator)
                settingsEntry(navigator)
            }
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                NavDisplay(
                    entries = appState.navigationState.toEntries(entryProvider),
                    onBack = navigator::goBack,
                )
            }
        }
    }
}