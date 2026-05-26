package dev.dl.demoapp.core.ble.internal

import android.Manifest
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
import dev.dl.demoapp.core.ble.BleDevice
import dev.dl.demoapp.core.ble.BleScanMode
import dev.dl.demoapp.core.ble.BleScanResult

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
internal fun ScanResult.toModel(): BleScanResult = BleScanResult(
    device = BleDevice(
        name = device.name,
        address = device.address
    ),
    rssi = rssi,
    scanRecord = scanRecord?.bytes,
)

internal fun BleScanMode.toAndroidMode(): Int {
    return when (this) {
        BleScanMode.LowPower ->
            ScanSettings.SCAN_MODE_LOW_POWER

        BleScanMode.Balanced ->
            ScanSettings.SCAN_MODE_BALANCED

        BleScanMode.LowLatency ->
            ScanSettings.SCAN_MODE_LOW_LATENCY
    }
}