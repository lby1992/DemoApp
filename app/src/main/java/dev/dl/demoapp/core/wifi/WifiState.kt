package dev.dl.demoapp.core.wifi

sealed interface WifiState {
    data object Idle : WifiState
    data object Connecting : WifiState
    data class Connected(
        val ssid: String,
    ) : WifiState

    data object Lost : WifiState
    data class ConnectedFailed(
        val error: WifiException,
    ) : WifiState
}