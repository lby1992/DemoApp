package dev.dl.demoapp.core.common.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultPaginator<T, Key>(
    private val initialKey: Key?,
    private val pageSize: Int,
    private val dataSource: PagingDataSource<Key, T>,
    private val coroutineScope: CoroutineScope,
) : Paginator<T> {
    private val _items = MutableStateFlow<List<T>>(emptyList())
    override val items: StateFlow<List<T>> = _items.asStateFlow()

    private val _state = MutableStateFlow<LoadState>(LoadState.Idle)
    override val state: StateFlow<LoadState> = _state.asStateFlow()

    private var currentKey: Key? = initialKey
    private var endReached = false
    private var isLoading = false

    override fun loadNext() {
        if (isLoading || endReached) return

        isLoading = true
        _state.value = if (_items.value.isEmpty()) {
            LoadState.Loading
        } else {
            LoadState.Appending
        }

        coroutineScope.launch {
            try {
                val result = dataSource.load(key = currentKey, pageSize = pageSize)
                val newList = _items.value + result.data
                _items.value
            } catch (e: Exception) {

            } finally {
                isLoading = false
            }
        }
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    override fun retry() {
        loadNext()
    }
}