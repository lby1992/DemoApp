package dev.dl.demoapp.feature.expense.detail

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ExpenseDetailViewModel.Factory::class)
class ExpenseDetailViewModel @AssistedInject constructor(
    @Assisted val expenseId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(expenseId: String): ExpenseDetailViewModel
    }
}