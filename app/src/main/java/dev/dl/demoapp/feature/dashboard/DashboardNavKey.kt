package dev.dl.demoapp.feature.dashboard

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator
import dev.dl.demoapp.feature.expense.ExpenseNavKey
import dev.dl.demoapp.feature.todos.TodosNavKey
import kotlinx.serialization.Serializable

@Serializable
data object DashboardNavKey : NavKey

fun EntryProviderScope<NavKey>.dashboardEntry(navigator: Navigator) = entry<DashboardNavKey> {
    DashboardScreen(
        navigateToTodos = { navigator.navigateTo(TodosNavKey) },
        navigateToExpense = { navigator.navigateTo(ExpenseNavKey) },
    )
}