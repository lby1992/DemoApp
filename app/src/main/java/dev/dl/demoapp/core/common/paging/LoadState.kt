package dev.dl.demoapp.core.common.paging

sealed interface LoadState {
    data object Idle : LoadState

    /**
     * Loading state without presentation of any items.
     */
    data object Loading : LoadState

    data object Refreshing : LoadState

    data object Appending : LoadState

    data class Error(val throwable: Throwable) : LoadState

    data object EndReached : LoadState
}