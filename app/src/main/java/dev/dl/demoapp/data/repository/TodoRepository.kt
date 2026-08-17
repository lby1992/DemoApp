package dev.dl.demoapp.data.repository

import dev.dl.demoapp.data.local.database.FakeLocalTodoDataSource
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val dataSource: FakeLocalTodoDataSource,
) {
//    fun getTodos(): Flow<PagingData<Todo>> {
//        return Pager(
//            config = PagingConfig(pageSize = 10),
//            pagingSourceFactory = {
//
//            },
//        )
//    }
}