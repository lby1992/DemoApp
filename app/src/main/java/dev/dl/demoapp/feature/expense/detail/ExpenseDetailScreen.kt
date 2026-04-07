package dev.dl.demoapp.feature.expense.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun ExpenseDetailScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit,
    viewModel: ExpenseDetailViewModel = hiltViewModel(),
) {
    ExpenseDetailScreenContent(
        modifier = modifier,
        goBack = goBack,
        viewModel = viewModel,
    )
}

@Composable
private fun ExpenseDetailScreenContent(
    modifier: Modifier,
    goBack: () -> Unit,
    viewModel: ExpenseDetailViewModel,
) {
    DummyScreen(
        "Expense Detail: ${viewModel.expenseId}",
        navigations = listOf(
            "Back" to { goBack() },
        )
    )
}