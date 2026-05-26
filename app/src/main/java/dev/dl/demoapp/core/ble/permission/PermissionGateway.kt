package dev.dl.demoapp.core.ble.permission

internal interface PermissionGateway {
    fun hasPermission(
        permission: String,
    ): Boolean
}