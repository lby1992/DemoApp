package dev.dl.demoapp.core.ble

import java.util.UUID

data class BleDevice(
    val name: String?,
    val address: String,
)

data class BleScanResult(
    val device: BleDevice,
    val rssi: Int,
    val scanRecord: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BleScanResult

        if (rssi != other.rssi) return false
        if (device != other.device) return false
        if (!scanRecord.contentEquals(other.scanRecord)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rssi
        result = 31 * result + device.hashCode()
        result = 31 * result + (scanRecord?.contentHashCode() ?: 0)
        return result
    }
}

sealed interface BleConnectionState {

    data object Connecting : BleConnectionState

    data object Connected : BleConnectionState

    data object Disconnected : BleConnectionState

    data class Failed(
        val throwable: Throwable
    ) : BleConnectionState
}

enum class BleScanMode {
    LowPower,
    Balanced,
    LowLatency
}

data class BleScanConfig(
    val serviceUuids: List<UUID> = emptyList(),
    val scanMode: BleScanMode = BleScanMode.LowLatency,
)

enum class BlePermission {
    Scan,
    Connect
}