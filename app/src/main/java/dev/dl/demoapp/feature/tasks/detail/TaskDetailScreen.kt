package dev.dl.demoapp.feature.tasks.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun TodoDetailScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit,
    viewModel: TodoDetailViewModel = hiltViewModel(),
) {
    TodoDetailContent(
        modifier = modifier,
        goBack = goBack,
        viewModel = viewModel,
    )
}

@Composable
private fun TodoDetailContent(
    modifier: Modifier = Modifier,
    goBack: () -> Unit,
    viewModel: TodoDetailViewModel,
) {
    DummyScreen(
        title = "Todo Detail: ${viewModel.todoId}",
        navigations = listOf(
            "Go Back" to goBack,
        ),
    )
}