package dev.dl.demoapp.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    navigateToTodos: () -> Unit,
    navigateToExpense: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    DashboardScreenContent(
        modifier = modifier,
        navigateToTodos = navigateToTodos,
        navigateToExpense = navigateToExpense,
    )
}

@Composable
private fun DashboardScreenContent(
    modifier: Modifier = Modifier,
    navigateToTodos: () -> Unit,
    navigateToExpense: () -> Unit,
) {
    DummyScreen(
        "Dashboard",
        navigations = listOf(
            "Todos" to { navigateToTodos() },
            "Expense" to { navigateToExpense() },
        )
    )
}