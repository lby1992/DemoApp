package dev.dl.demoapp.feature.todos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.dl.demoapp.dev.DummyScreen

@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    goToTodoDetail: (todoId: String) -> Unit,
    viewModel: TodosViewModel = hiltViewModel(),
) {
    TodosContent(
        modifier = modifier,
        goToTodoDetail = goToTodoDetail,
        viewModel = viewModel,
    )
}

@Composable
private fun TodosContent(
    modifier: Modifier = Modifier,
    goToTodoDetail: (todoId: String) -> Unit,
    viewModel: TodosViewModel,
) {
    DummyScreen(
        title = "Todos Screen",
        navigations = listOf(
            "Go to Todo Detail" to { goToTodoDetail("todoId") },
        )
    )
}