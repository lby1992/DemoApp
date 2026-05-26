package dev.dl.demoapp.core.ble.permission

class FakePermissionGateway(
    private val granted: Set<String>,
): PermissionGateway {
    override fun hasPermission(permission: String): Boolean {
        return permission in granted
    }
}