package dev.dl.demoapp.feature.expense

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator
import dev.dl.demoapp.feature.expense.detail.ExpenseDetailNavKey
import kotlinx.serialization.Serializable

@Serializable
data object ExpenseNavKey : NavKey

fun EntryProviderScope<NavKey>.expenseEntry(navigator: Navigator) = entry<ExpenseNavKey> {
    ExpenseScreen(
        goToDetail = { expenseId ->
            navigator.navigateTo(ExpenseDetailNavKey(expenseId))
        },
    )
}