package dev.dl.demoapp.feature.expense

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun ExpenseScreen(
    modifier: Modifier = Modifier,
    goToDetail: (String) -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel(),
) {
    ExpenseScreenContent(modifier, goToDetail)
}

@Composable
private fun ExpenseScreenContent(
    modifier: Modifier,
    goToDetail: (String) -> Unit,
) {
    DummyScreen(
        "Expense",
        navigations = listOf(
            "Expense Detail 1" to { goToDetail("1") },
            "Expense Detail 2" to { goToDetail("2") },
            "Expense Detail 3" to { goToDetail("3") },
        )
    )
}