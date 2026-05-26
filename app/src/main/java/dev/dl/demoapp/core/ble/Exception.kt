package dev.dl.demoapp.core.ble

import java.io.IOException

sealed class BleException : IOException()

class BluetoothUnsupportedException : BleException()

class BluetoothPermissionMissingException(
    val permissions: List<BlePermission>,
) : BleException()

class BluetoothDisabledException : BleException()

class BleScanFailedException(
    val code: Int
) : BleException()

class BleConnectionTimeoutException : BleException()

class BleConnectionFailedException(
    val status: Int
) : BleException()