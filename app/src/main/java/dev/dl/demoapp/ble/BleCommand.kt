package dev.dl.demoapp.ble

interface BleCommand<R> {

    val request: ByteArray

    fun match(
        packet: ByteArray,
    ): Boolean

    fun parse(
        packet: ByteArray,
    ): R
}