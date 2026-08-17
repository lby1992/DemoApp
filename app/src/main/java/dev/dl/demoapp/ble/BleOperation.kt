package dev.dl.demoapp.ble

import kotlinx.coroutines.CompletableDeferred

sealed interface BleOperation {
    class Connect(
        val address: String,
        val deferred: CompletableDeferred<Unit>,
    ): BleOperation

    class Command<R>(
        val command: BleCommand<R>,
        val deferred: CompletableDeferred<R>
    ): BleOperation
}