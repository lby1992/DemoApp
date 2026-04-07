package dev.dl.demoapp.feature.todos

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator
import dev.dl.demoapp.feature.todos.detail.TodoDetailNavKey
import kotlinx.serialization.Serializable

@Serializable
data object TodosNavKey : NavKey


fun EntryProviderScope<NavKey>.todosEntry(
    navigator: Navigator,
) = entry<TodosNavKey> {
    TodosScreen(
        goToTodoDetail = { todoId ->
            navigator.navigateTo(TodoDetailNavKey(todoId))
        },
    )
}