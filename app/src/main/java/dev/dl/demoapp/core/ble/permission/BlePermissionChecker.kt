package dev.dl.demoapp.core.ble.permission

import dev.dl.demoapp.core.ble.BlePermission

internal class BlePermissionChecker(
    private val gateway: PermissionGateway,
    private val resolver: BlePermissionResolver,
) {

    fun missingPermissions(
        permissions: Collection<BlePermission>
    ): List<BlePermission> {

        return permissions.filterNot { permission ->
            val androidPermissions = resolver.resolve(permission)

            androidPermissions.all { gateway.hasPermission(it) }
        }
    }
}