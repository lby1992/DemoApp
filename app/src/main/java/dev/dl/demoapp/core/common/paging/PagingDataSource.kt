package dev.dl.demoapp.core.common.paging

interface PagingDataSource<Key, T> {
    suspend fun load(
        key: Key?,
        pageSize: Int,
    ): PaginatedResult<T, Key>
}