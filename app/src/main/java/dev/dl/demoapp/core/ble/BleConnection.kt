package dev.dl.demoapp.core.ble

import android.Manifest
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.StateFlow

interface BleConnection {
    val device: BleDevice

    val state: StateFlow<BleConnectionState>

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun disconnect()
}