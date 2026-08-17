package dev.dl.demoapp.data.local.database

import dev.dl.demoapp.core.common.paging.PaginatedResult
import dev.dl.demoapp.core.common.paging.PagingDataSource
import dev.dl.demoapp.data.model.Todo

class FakeLocalTodoDataSource : PagingDataSource<Int, Todo> {
    private val all = List(1000) {
        Todo(
            id = it,
            title = "Task: $it",
            done = it % 7 == 0,
        )
    }

    override suspend fun load(
        key: Int?,
        pageSize: Int
    ): PaginatedResult<Todo, Int> {
        val page = key ?: 0
        val offset = page * pageSize

        val data = all.drop(offset).take(pageSize)
        val nextKey = if (offset + pageSize >= all.size) null else page + 1

        return PaginatedResult(
            data = data,
            nextKey = nextKey,
        )
    }
}