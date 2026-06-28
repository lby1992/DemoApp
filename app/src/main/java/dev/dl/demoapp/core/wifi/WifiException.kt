package dev.dl.demoapp.core.wifi

sealed class WifiException : RuntimeException() {
    class WifiDisabled: WifiException()
    class LocationDisabled: WifiException()
    class PermissionMissing: WifiException()
    class Failed(val msg: String? = null): WifiException()
    class Unavailable: WifiException()
}