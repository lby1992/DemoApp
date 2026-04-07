package dev.dl.demoapp.feature.todos.detail

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator

data class TodoDetailNavKey(
    val todoId: String,
) : NavKey

fun EntryProviderScope<NavKey>.taskDetailEntry(
    navigator: Navigator,
) = entry<TodoDetailNavKey> { key ->
    val todoId = key.todoId
    TodoDetailScreen(
        goBack = { navigator.goBack() },
        viewModel = hiltViewModel<TodoDetailViewModel, TodoDetailViewModel.Factory> {
            it.create(todoId)
        }
    )
}
