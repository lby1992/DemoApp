package dev.dl.demoapp.core.ble

import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface BleClient {
    fun scan(
        config: BleScanConfig,
    ): Flow<BleScanResult>

    suspend fun connect(
        address: String,
        timeout: Duration,
    ): BleConnection
}