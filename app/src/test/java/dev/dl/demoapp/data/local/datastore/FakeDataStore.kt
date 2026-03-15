package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * An in-memory implementation of [DataStore] used for unit testing.
 *
 * Data is stored in a [MutableStateFlow] instead of disk.
 */
class FakeDataStore<T>(initialValue: T) : DataStore<T> {
    private val _data = MutableStateFlow<T>(initialValue)
    override val data: Flow<T> = _data

    override suspend fun updateData(transform: suspend (t: T) -> T) =
        _data.updateAndGet { transform(it) }
}