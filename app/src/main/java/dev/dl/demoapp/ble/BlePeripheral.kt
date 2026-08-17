package dev.dl.demoapp.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okio.IOException

class BlePeripheral(
    private val address: String,
    private val bluetoothAdapter: BluetoothAdapter,
    private val appContext: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val operationQueue = Channel<BleOperation>(Channel.UNLIMITED)

    private val _events = MutableSharedFlow<BleEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val events = _events.asSharedFlow()

    private val _deviceEvents = MutableSharedFlow<ByteArray>()

    // pushed by the device
    val deviceEvents = _deviceEvents.asSharedFlow()

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS &&
                newState == BluetoothProfile.STATE_CONNECTED
            ) {
                _events.tryEmit(BleEvent.Connected)
            } else {
                _events.tryEmit(BleEvent.Disconnected)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            _events.tryEmit(BleEvent.WriteComplete(status))
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            _events.tryEmit(BleEvent.Notify(value))
        }
    }

    init {
        scope.launch {
            for (operation in operationQueue) {
                try {
                    when (operation) {
                        is BleOperation.Connect -> handleConnect(operation)
                        is BleOperation.Command<*> -> handleCommand(operation)
                    }
                } catch (e: Exception) {
                    when (operation) {
                        is BleOperation.Connect -> operation.deferred.completeExceptionally(e)
                        is BleOperation.Command<*> -> operation.deferred.completeExceptionally(e)
                    }
                }
            }
        }
    }

    suspend fun connect(
        address: String
    ) {
        val deferred = CompletableDeferred<Unit>()

        operationQueue.send(
            BleOperation.Connect(address, deferred)
        )
        deferred.await()
    }

    private suspend fun handleConnect(op: BleOperation.Connect) {
        withTimeout(15000L) {
            val device = bluetoothAdapter.getRemoteDevice(op.address)
//            gatt = device.connectGatt(
//                appContext,
//                false,
//                gattCallback,
//            )
            events.first {
                when (it) {
                    BleEvent.Connected -> true
                    BleEvent.Disconnected -> throw IOException("Connect failed") //TODO
                    else -> false
                }
            }

            op.deferred.complete(Unit)
        }
    }

    suspend fun <R> execute(
        command: BleCommand<R>,
    ): R {
        val deferred = CompletableDeferred<R>()

        operationQueue.send(
            BleOperation.Command(
                command,
                deferred,
            )
        )

        return deferred.await()
    }

    private suspend fun <R> handleCommand(
        op: BleOperation.Command<R>,
    ) {
        writeInternal(op.command.request)

        val packet = withTimeout(5000L) {
            events.filterIsInstance<BleEvent.Notify>()
                .map { it.data }
                .first {
                    op.command.match(it)
                }
        }

        op.deferred.complete(op.command.parse(packet))
    }

    private suspend fun writeInternal(
        data: ByteArray,
    ) {

    }
}