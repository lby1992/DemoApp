package dev.dl.demoapp.core.ble.internal

import android.os.Build

internal interface PlatformInfo {
    val sdkVersion: Int
}

internal object AndroidPlatformInfo : PlatformInfo {
    override val sdkVersion: Int = Build.VERSION.SDK_INT
}