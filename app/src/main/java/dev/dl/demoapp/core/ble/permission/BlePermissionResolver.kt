package dev.dl.demoapp.core.ble.permission

import android.Manifest
import android.os.Build
import dev.dl.demoapp.core.ble.BlePermission
import dev.dl.demoapp.core.ble.internal.PlatformInfo

internal class BlePermissionResolver(
    private val platformInfo: PlatformInfo,
) {
    fun resolve(
        permission: BlePermission,
    ): List<String> {
        return when (permission) {
            BlePermission.Scan -> {
                if (platformInfo.sdkVersion >= Build.VERSION_CODES.S) {
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN
                    )

                } else {
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }

            BlePermission.Connect -> {
                if (platformInfo.sdkVersion >= Build.VERSION_CODES.S) {
                    listOf(
                        Manifest.permission.BLUETOOTH_CONNECT
                    )

                } else {
                    emptyList()
                }
            }
        }
    }
}