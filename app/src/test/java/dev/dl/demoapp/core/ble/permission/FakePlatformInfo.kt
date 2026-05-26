package dev.dl.demoapp.core.ble.permission

import dev.dl.demoapp.core.ble.internal.PlatformInfo

class FakePlatformInfo(override val sdkVersion: Int) : PlatformInfo {
}