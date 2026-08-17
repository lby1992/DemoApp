package dev.dl.demoapp.core.common.paging

import kotlinx.coroutines.flow.StateFlow

interface Paginator<T> {
    val items: StateFlow<List<T>>

    val state: StateFlow<LoadState>

    fun loadNext()

    fun refresh()

    fun retry()
}