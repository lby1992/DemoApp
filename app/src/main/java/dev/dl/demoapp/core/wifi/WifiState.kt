package dev.dl.demoapp.core.wifi

import android.net.Network

sealed interface WifiState {
    data object Idle : WifiState
    data object Connecting : WifiState
    data class Connected(
        val ssid: String,
    ) : WifiState

    data object Lost : WifiState
    data class Error(
        val error: WifiException,
    ): WifiState
}