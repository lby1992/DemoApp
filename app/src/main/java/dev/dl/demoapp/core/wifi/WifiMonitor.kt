package dev.dl.demoapp.core.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object WifiMonitor {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager

    fun init(context: Context) {
        connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        wifiManager = context.getSystemService(WifiManager::class.java)

        observeWifiStatus()

        _currentSsid.value = snapshotCurrentSsid()
    }

    private val _currentSsid = MutableStateFlow<String?>(null)

    val currentSsid = _currentSsid.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun updateCurrentSsidManually(network: Network) {
        _currentSsid.value = snapshotCurrentSsid(network)
    }

    private fun observeWifiStatus() {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.i("WifiMonitor", "xxx onAvailable")

                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val isWifi =
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

                if (isWifi) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
                        _currentSsid.value = wifiInfo?.ssid

                    } else {
                        _currentSsid.value = wifiManager.connectionInfo.ssid
                    }
                } else {
                    _currentSsid.value = null
                }

            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.i("WifiMonitor", "xxx onCapabilitiesChanged: $networkCapabilities")

                val isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

                if (isWifi) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
                        _currentSsid.value = wifiInfo?.ssid

                    } else {
                        _currentSsid.value = wifiManager.connectionInfo.ssid
                    }
                } else {
                    _currentSsid.value = null
                }
            }

            override fun onLost(network: Network) {
                Log.i("WifiMonitor", "xxx onLost")
                _currentSsid.value = null
            }
        }
            .also {
                networkCallback = it
            }

        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    // 获取当前ssid需要fine_location
    private fun snapshotCurrentSsid(network: Network? = null): String? {
        val activeNetwork = network ?: connectivityManager.activeNetwork ?: return null
        val currentCaps = connectivityManager.getNetworkCapabilities(activeNetwork)

        if (currentCaps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) != true) return null

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val wifiInfo = currentCaps.transportInfo as? WifiInfo
            wifiInfo?.ssid
        } else {
            wifiManager.connectionInfo.ssid
        }
    }
}