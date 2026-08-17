package dev.dl.demoapp.core.common.ui.paging

/**
 * UI state for a paginated list with loading, refreshing, and error states.
 */
data class ListLoadState<T>(
    val items: List<T> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: Throwable? = null,
    val loadMoreError: Throwable? = null,
    val hasMore: Boolean = true,
)

/**
 * Actions exposed to the UI.
 */
interface ListLoadActions {
    fun refresh()
    fun retry()
    fun loadMore()
    fun retryLoadMore()
}