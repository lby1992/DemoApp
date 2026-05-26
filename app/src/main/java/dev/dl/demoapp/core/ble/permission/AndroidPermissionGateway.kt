package dev.dl.demoapp.core.ble.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal class AndroidPermissionGateway(
    private val appContext: Context,
) : PermissionGateway {
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}