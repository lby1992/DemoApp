package dev.dl.demoapp.feature.expense.detail

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.dl.demoapp.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseDetailNavKey(
    val expenseId: String,
) : NavKey

fun EntryProviderScope<NavKey>.expenseDetailEntry(
    navigator: Navigator,
) = entry<ExpenseDetailNavKey> { navKey ->
    val expenseId = navKey.expenseId
    ExpenseDetailScreen(
        goBack = { navigator.goBack() },
        viewModel = hiltViewModel<ExpenseDetailViewModel, ExpenseDetailViewModel.Factory> {
            it.create(expenseId)
        }
    )
}