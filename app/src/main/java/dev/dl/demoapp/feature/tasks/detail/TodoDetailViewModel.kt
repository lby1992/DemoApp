package dev.dl.demoapp.feature.tasks.detail

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = TodoDetailViewModel.Factory::class)
class TodoDetailViewModel @AssistedInject constructor(
    @Assisted val todoId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(todoId: String): TodoDetailViewModel
    }
}