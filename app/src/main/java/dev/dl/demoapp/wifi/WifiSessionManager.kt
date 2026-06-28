package dev.dl.demoapp.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.dl.demoapp.core.wifi.WifiException
import dev.dl.demoapp.core.wifi.WifiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object WifiSessionManager {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager
    private lateinit var locationService: LocationManager

    private val defaultCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.i(TAG, "default onAvailable")
            val currentState = _connectState.value
            if (currentState is WifiState.Connected) {
                val newSsid = snapshotSsid(network)
                if (newSsid != currentState.ssid) {
                    updateState(WifiState.Lost)
                } else {
                    // Do nothing
                }
            } else if (currentState == WifiState.Connecting) {
                updateState(WifiState.Lost) // There may be a better state for this.
            } else {
                val newSsid = snapshotSsid(network) ?: "" // TODO If we can't obtain the ssid, how to deal with it?
                updateState(WifiState.Connected(newSsid))
            }
        }

        override fun onLost(network: Network) {
            Log.i(TAG, "default onLost")
        }

        override fun onUnavailable() {
            Log.i(TAG, "default onUnavailable")
        }
    }

    // ============== Above API 29 ===============
    private var requestCallback: ConnectivityManager.NetworkCallback? = null

    // ============== Below API 29 ===============
    private var previousNetworkId: Int? = null
    private var previousSsid: String? = null

    private val _connectState = MutableStateFlow<WifiState>(WifiState.Idle)
    val connectState = _connectState.asStateFlow()

    val isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled

    val isLocationEnabled: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationService.isLocationEnabled
        } else {
            locationService.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    fun init(context: Context) {
        with(context.applicationContext) {
            connectivityManager = getSystemService(ConnectivityManager::class.java)
            wifiManager = getSystemService(WifiManager::class.java)
            locationService = getSystemService(LocationManager::class.java)
        }

        connectivityManager.registerDefaultNetworkCallback(defaultCallback)
    }

    // 进入Wifi相关页面先请求一次当前状态
    // 在这个时候可以申请权限和打开系统开关
    @Throws(WifiException::class)
    fun checkStatus(context: Context) {
        checkForRequiredPermissions(context)
        checkForRequiredSystemServices()

        val ssid = snapshotSsid()
        if (ssid != null) {
            updateState(WifiState.Connected(ssid))
        } else {
            updateState(WifiState.Idle)
        }
    }

    @Throws(WifiException::class)
    fun connectTo(
        ssid: String,
        password: String,
    ) {
        val currentState = _connectState.value
        if (currentState == WifiState.Connecting
            || (currentState is WifiState.Connected && currentState.ssid == ssid)
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connect(ssid, password)
        } else {
            connectLegacy(ssid, password)
        }
    }

    fun disconnect() {
        runCatching {
            connectivityManager.bindProcessToNetwork(null)
            requestCallback?.let(connectivityManager::unregisterNetworkCallback)
            requestCallback = null

            // TODO legacy?
        }
    }

    fun cleanup() {
        connectivityManager.unregisterNetworkCallback(defaultCallback)

        disconnect()
    }

    private fun checkForRequiredPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CHANGE_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw WifiException.PermissionMissing()
        }
    }

    @Throws(WifiException::class)
    private fun checkForRequiredSystemServices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No need of wifi enabled for status checking
//            if (!isWifiEnabled) {
//                throw WifiException.WifiDisabled()
//            }

            if (!isLocationEnabled) {
                throw WifiException.LocationDisabled()
            }
//        } else {
//            if (!wifiManager.isWifiEnabled && !wifiManager.setWifiEnabled(true)) {
//                throw WifiException.WifiDisabled()
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connect(
        ssid: String,
        password: String
    ) {
        try {
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            val request = NetworkRequest.Builder()
                .addTransportType(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build()
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.i(TAG, "request onAvailable")

                    if (connectivityManager.bindProcessToNetwork(network)) {
                        updateState(WifiState.Connected(ssid))
                    } else {
                        Log.w(TAG, "failed to bind process to network")
                        updateState(WifiState.ConnectedFailed(WifiException.Failed("Connect failed.")))
                    }
                }

                override fun onLost(network: Network) {
                    Log.i(TAG, "request onLost")
                    updateState(WifiState.Lost)

                }

                override fun onUnavailable() {
                    Log.i(TAG, "request onUnavailable")

                    updateState(WifiState.ConnectedFailed(WifiException.Unavailable()))
                }
            }.also {
                requestCallback = it
                connectivityManager.requestNetwork(request, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw when (e) {
                is IllegalArgumentException -> WifiException.Failed("request contains invalid network capabilities ==> $e")
                is SecurityException -> WifiException.PermissionMissing()
                is RuntimeException -> WifiException.Failed("the app already has too many callbacks registered ==> $e")
                else -> WifiException.Failed("Unknown error ==> $e")
            }
        }
    }

    private fun connectLegacy(
        ssid: String,
        password: String
    ) {
        val config = WifiConfiguration()
            .apply {
                SSID = "\"$ssid\""
                preSharedKey = "\"$password\""
                allowedKeyManagement.set(
                    WifiConfiguration.KeyMgmt.WPA_PSK
                )
            }

        val networkId = wifiManager.addNetwork(config)
        if (networkId == -1) {
            throw WifiException.Failed()
        } else {
            val info = wifiManager.connectionInfo
            previousNetworkId = info.networkId
            previousSsid = info.ssid
            wifiManager.disconnect()

//            delay(200L)

            if (!wifiManager.enableNetwork(networkId, true)) {
                // 此处attemptConnect为true的话，是否还需要reconnect
                throw WifiException.Failed()
            }

//            delay(200L)

            if (!wifiManager.reconnect()) {
                throw WifiException.Failed()
            }

        }
    }

    private fun snapshotSsid(network: Network? = null): String? {
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

    private fun updateState(newState: WifiState) {
        _connectState.value = newState
    }

    private const val TAG = "WifiSessionManager"
}