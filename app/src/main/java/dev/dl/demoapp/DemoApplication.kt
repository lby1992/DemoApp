package dev.dl.demoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.dl.demoapp.wifi.WifiSessionManager

/**
 * Represent the application
 */
@HiltAndroidApp
class DemoApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        WifiSessionManager.init(applicationContext)
    }
}