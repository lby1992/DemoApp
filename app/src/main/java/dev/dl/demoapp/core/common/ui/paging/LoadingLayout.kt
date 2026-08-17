package dev.dl.demoapp.core.common.ui.paging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun <T> LoadingLayout(
    state: ListLoadState<T>,
    actions: ListLoadActions,
    shouldLoadMore: Boolean,
    modifier: Modifier = Modifier,

    emptyContent: @Composable () -> Unit = { DefaultEmpty() },
    errorContent: @Composable (retry: () -> Unit) -> Unit = { DefaultError(it) },
    loadingContent: @Composable () -> Unit = { DefaultLoading() },
    footer: @Composable () -> Unit,

    content: @Composable () -> Unit
) {
    val isEmpty = state.items.isEmpty()

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state.hasMore && !state.isLoadingMore && state.loadMoreError == null) {
            actions.loadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = actions::refresh,
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isEmpty && state.isRefreshing -> loadingContent()
            isEmpty && state.error != null -> errorContent(actions::retry)
            isEmpty -> emptyContent()
            else -> content()
        }
    }
}