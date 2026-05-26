package dev.dl.demoapp.core.ble.internal

import android.Manifest
import android.bluetooth.BluetoothGatt
import androidx.annotation.RequiresPermission
import dev.dl.demoapp.core.ble.BleConnection
import dev.dl.demoapp.core.ble.BleConnectionState
import dev.dl.demoapp.core.ble.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class RealBleConnection(
    override val device: BleDevice,
    private val gatt: BluetoothGatt,
) : BleConnection {

    private val _state = MutableStateFlow<BleConnectionState>(BleConnectionState.Connected)

    override val state: StateFlow<BleConnectionState> = _state.asStateFlow()

    internal fun updateState(
        newState: BleConnectionState,
    ) {
        _state.value = newState
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun disconnect() {
        gatt.disconnect()
        gatt.close()

        _state.value = BleConnectionState.Disconnected
    }
}