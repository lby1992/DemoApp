package dev.dl.demoapp.ble

sealed interface BleEvent {
    data object Connected : BleEvent
    data object Disconnected : BleEvent
    data class Notify(
        val data: ByteArray,
    ) : BleEvent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Notify

            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    data class WriteComplete(
        val status: Int
    ) : BleEvent
}