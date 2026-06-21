package dev.dl.demoapp.core.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class WifiConnector(
    private val context: Context
) {

    private val tag = "WifiConnector"

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun scan(): List<String> = suspendCancellableCoroutine { continuation ->
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val updated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (updated) {
                    try {
                        val results = wifiManager.scanResults
                        val wifiInfoList = results.map {
                            "SSID: ${it.SSID}, BSSID: ${it.BSSID}, level: ${it.level}, frequency: ${it.frequency}"
                        }
                        if (continuation.isActive) {
                            continuation.resume(wifiInfoList) { cause, _, _ -> }
                        }
                    } catch (e: SecurityException) {
                        // Permission denied
                        Log.w(tag, "Permission denied", e)
                        scanFailure(continuation, ScanError.PermissionDenied())
                    }
                } else {
                    scanFailure(continuation, ScanError.NotUpdated())
                    Log.w(tag, "not updated.")
                }
            }
        }

        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        ContextCompat.registerReceiver(
            context,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )


        val success = wifiManager.startScan()
        if (!success) { // Location Service may be turned off
            scanFailure(continuation, ScanError.CantStart())
        }

        continuation.invokeOnCancellation {
            context.unregisterReceiver(receiver)
            Log.i("Wifi connector", "Invoke on cancellation")
        }
    }

    private fun scanFailure(continuation: CancellableContinuation<List<String>>, error: ScanError) {
        if (continuation.isActive) {
            continuation.resumeWithException(error)
        }
    }
}

sealed class ScanError : RuntimeException() {
    class PermissionDenied : ScanError()
    class NotUpdated : ScanError()
    class CantStart : ScanError()
}