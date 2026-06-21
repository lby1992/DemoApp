package dev.dl.demoapp.core.wifi

sealed class WifiException : RuntimeException() {
    class Disabled: WifiException()
    class PermissionMissing: WifiException()
    class Failed(val msg: String? = null): WifiException()
    class Unavailable: WifiException()
    class AlreadyConnected: WifiException()
}