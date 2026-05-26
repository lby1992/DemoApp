package dev.dl.demoapp.core.ble.permission

import android.Manifest
import dev.dl.demoapp.core.ble.BlePermission
import junit.framework.TestCase.assertTrue
import org.junit.Test


class BlePermissionCheckerTest {
    @Test
    fun `scan should fail when scan permission missing`() {
        val gateway = FakePermissionGateway(emptySet())
        val resolver = BlePermissionResolver(FakePlatformInfo(31))
        val checker = BlePermissionChecker(gateway, resolver)
        val missing = checker.missingPermissions(listOf(BlePermission.Scan))

        assertTrue(
            missing.contains(BlePermission.Scan)
        )
    }

    @Test
    fun `scan should pass when scan permission granted on Android 12`() {
        val gateway = FakePermissionGateway(setOf(Manifest.permission.BLUETOOTH_SCAN))
        val resolver = BlePermissionResolver(FakePlatformInfo(31))
        val checker = BlePermissionChecker(gateway, resolver)
        val missing = checker.missingPermissions(listOf(BlePermission.Scan))

        assertTrue(
            missing.isEmpty()
        )
    }
}