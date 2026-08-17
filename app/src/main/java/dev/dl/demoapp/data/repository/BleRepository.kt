package dev.dl.demoapp.data.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.observer.ConnectionObserver
import timber.log.Timber
import java.util.UUID


class BleRepository {
}

sealed interface BleDeviceState {
    object Disconnected : BleDeviceState
    object Connecting : BleDeviceState
    object Ready : BleDeviceState
    data class Failed(val reason: String) : BleDeviceState
}

data class BleDevice(
    val id: String,
    val name: String?,
)

class InternalBleManager(
    context: Context
) : BleManager(context) {
    private val _state = MutableStateFlow<BleDeviceState>(BleDeviceState.Disconnected)
    val state = _state.asStateFlow()

    init {
        connectionObserver = object : ConnectionObserver {
            override fun onDeviceConnecting(device: BluetoothDevice) {
                _state.value = BleDeviceState.Connecting
            }

            override fun onDeviceConnected(device: BluetoothDevice) {
                TODO("Not yet implemented")
            }

            override fun onDeviceFailedToConnect(
                device: BluetoothDevice,
                reason: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun onDeviceReady(device: BluetoothDevice) {
                TODO("Not yet implemented")
            }

            override fun onDeviceDisconnecting(device: BluetoothDevice) {
                TODO("Not yet implemented")
            }

            override fun onDeviceDisconnected(
                device: BluetoothDevice,
                reason: Int
            ) {
                TODO("Not yet implemented")
            }
        }
    }

    // ==== Logging ====
    override fun getMinLogPriority(): Int {
        // Use to return minimal desired logging priority.
        return Log.VERBOSE
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    // ==== Required Implementation ====

    // This is a reference to a characteristic that the manager will use internally.
    private var fluxCapacitorControlPoint: BluetoothGattCharacteristic? = null

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        // TODO: 校验 service / characteristic
        return gatt.getService(FLUX_SERVICE_UUID)?.let { service ->
            fluxCapacitorControlPoint = service.getCharacteristic(FLUX_CHAR_UUID)
            fluxCapacitorControlPoint != null
        } ?: false
    }

    // =========================
    // 🚀 初始化（服务发现后）
    // =========================
    override fun initialize() {
        // 可以在这里：
        // - enable notification
        // - request MTU
        // - read 初始数据
        requestMtu(517)
            .enqueue()
    }

    override fun onServicesInvalidated() {
    }

    override fun onDeviceReady() {
        super.onDeviceReady()
    }

    companion object {
        private val FLUX_SERVICE_UUID = UUID.randomUUID() // TODO: Use real service uuid.
        private val FLUX_CHAR_UUID = UUID.randomUUID() // TODO: Use real characteristic uuid.
    }
}