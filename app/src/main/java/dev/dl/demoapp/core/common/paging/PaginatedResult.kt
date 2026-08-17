package dev.dl.demoapp.core.common.paging

data class PaginatedResult<T, Key>(
    val data: List<T>,
    val nextKey: Key?,
) {
    val endReached = nextKey == null
}