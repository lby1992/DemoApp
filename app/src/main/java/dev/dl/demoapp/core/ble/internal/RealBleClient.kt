package dev.dl.demoapp.core.ble.internal

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import androidx.annotation.VisibleForTesting
import dev.dl.demoapp.core.ble.BleClient
import dev.dl.demoapp.core.ble.BleConnection
import dev.dl.demoapp.core.ble.BleConnectionFailedException
import dev.dl.demoapp.core.ble.BleConnectionTimeoutException
import dev.dl.demoapp.core.ble.BleDevice
import dev.dl.demoapp.core.ble.BlePermission
import dev.dl.demoapp.core.ble.BleScanConfig
import dev.dl.demoapp.core.ble.BleScanFailedException
import dev.dl.demoapp.core.ble.BleScanResult
import dev.dl.demoapp.core.ble.BluetoothDisabledException
import dev.dl.demoapp.core.ble.BluetoothPermissionMissingException
import dev.dl.demoapp.core.ble.BluetoothUnsupportedException
import dev.dl.demoapp.core.ble.permission.AndroidPermissionGateway
import dev.dl.demoapp.core.ble.permission.BlePermissionChecker
import dev.dl.demoapp.core.ble.permission.BlePermissionResolver
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.time.withTimeout
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class RealBleClient(
    private val appContext: Context,
) : BleClient {

    private val bluetoothManager =
        appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val permissionChecker = BlePermissionChecker(
        gateway = AndroidPermissionGateway(appContext),
        resolver = BlePermissionResolver(AndroidPlatformInfo)
    )

    override fun scan(config: BleScanConfig): Flow<BleScanResult> = callbackFlow {
        if (bluetoothAdapter == null) { // 不支持蓝牙
            close(BluetoothUnsupportedException())
        } else if (!bluetoothAdapter.isEnabled) { // 蓝牙被禁用
            close(BluetoothDisabledException())
        } else {
            val scanner = bluetoothAdapter.bluetoothLeScanner
            val filters = config.serviceUuids.map {
                ScanFilter.Builder()
                    .setServiceUuid(
                        ParcelUuid(it)
                    )
                    .build()
            }
            val settings = ScanSettings.Builder()
                .setScanMode(config.scanMode.toAndroidMode())
                .build()

            val callback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    trySend(result.toModel())
                }

                override fun onScanFailed(errorCode: Int) {
                    close(BleScanFailedException(errorCode))
                }
            }

//            ensurePermissionsGranted {
//                scanner.startScan(filters, settings, callback)
//            }

            awaitClose {
                scanner.stopScan(callback)
            }
        }
    }

    override suspend fun connect(
        address: String,
        timeout: Duration
    ): BleConnection = if (bluetoothAdapter == null) { // 不支持蓝牙
        throw BluetoothUnsupportedException()
    } else if (!bluetoothAdapter.isEnabled) { // 蓝牙被禁用
        throw BluetoothDisabledException()
    } else {
        try {
            withTimeout(timeout) {
                suspendCancellableCoroutine { cont ->
                    val device = bluetoothAdapter.getRemoteDevice(address)
                    var gatt: BluetoothGatt? = null
                    val callback = object : BluetoothGattCallback() {
                        override fun onConnectionStateChange(
                            g: BluetoothGatt,
                            status: Int,
                            newState: Int
                        ) {
                            if (status != BluetoothGatt.GATT_SUCCESS) {
                                g.close()

                                if (cont.isActive) {
                                    cont.resumeWithException(BleConnectionFailedException(status))
                                }
                                return
                            }

                            when (newState) {
                                BluetoothProfile.STATE_CONNECTED -> {
                                    val connection = RealBleConnection(
                                        device = BleDevice(
                                            name = device.name,
                                            address = device.address,
                                        ),
                                        gatt = g,
                                    )
                                    if (cont.isActive) {
                                        cont.resume(connection)
                                    }
                                }

                                BluetoothProfile.STATE_DISCONNECTED -> {
                                    g.close()
                                }
                            }
                        }
                    }

                    ensurePermissionsGranted(listOf(BlePermission.Connect))
                    gatt = device.connectGatt(
                        appContext,
                        false,
                        callback,
                    )

                    cont.invokeOnCancellation {
                        ensurePermissionsGranted(listOf(BlePermission.Connect))
                        gatt?.close()
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            throw BleConnectionTimeoutException()
        }
    }

    @Throws(BluetoothPermissionMissingException::class)
    @VisibleForTesting
    fun ensurePermissionsGranted(permissions: List<BlePermission>) {
        val missing = permissionChecker.missingPermissions(
            permissions = permissions,
        )
        if (missing.isNotEmpty()) {
            throw BluetoothPermissionMissingException(missing)
        }
    }
}