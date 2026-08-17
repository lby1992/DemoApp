package dev.dl.demoapp.core.common.ui.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface PageResult<Key, Item> {
    data class Success<Key, Item>(
        val items: List<Item>,
        val nextKey: Key?,
        val hasMore: Boolean
    ) : PageResult<Key, Item>

    data class Error<Key, Item>(
        val error: Throwable
    ) : PageResult<Key, Item>
}

fun interface PageRequest<Key, Item> {
    suspend fun load(key: Key?): PageResult<Key, Item>
}

class Paginator<Key, Item>(
    private val initialKey: Key? = null,
    private val pageRequest: PageRequest<Key, Item>,
) {
    private val mutex = Mutex()

    private var currentKey: Key? = initialKey
    private var isLoading = false
    private var hasMore = true

    private val _state = MutableStateFlow(ListLoadState<Item>())
    val state: StateFlow<ListLoadState<Item>> = _state

    suspend fun refresh() {
        mutex.withLock {
            currentKey = initialKey
            hasMore = true

            _state.update {
                it.copy(isRefreshing = true, error = null, loadMoreError = null)
            }

            when (val result = pageRequest.load(initialKey)) {
                is PageResult.Success -> {
                    currentKey = result.nextKey
                    hasMore = result.hasMore
                    _state.update {
                        ListLoadState(
                            items = result.items,
                            isRefreshing = false,
                            hasMore = hasMore
                        )
                    }
                }

                is PageResult.Error -> {
                    _state.update { it.copy(isRefreshing = false, error = result.error) }
                }
            }
        }
    }

    suspend fun loadMore() {
        mutex.withLock {
            val current = _state.value

            if (current.isLoadingMore || current.isRefreshing || !hasMore) return

            when (val result = pageRequest.load(currentKey)) {
                is PageResult.Success -> {
                    currentKey = result.nextKey
                    hasMore = result.hasMore

                    _state.update {
                        it.copy(
                            items = it.items + result.items,
                            isLoadingMore = false,
                            hasMore = result.hasMore
                        )
                    }
                }

                is PageResult.Error -> {
                    _state.update {
                        it.copy(isLoadingMore = false, loadMoreError = result.error)
                    }
                }
            }
        }
    }

    fun reset() {
        currentKey = initialKey
        hasMore = true
        _state.value = ListLoadState()
    }
}