package dev.dl.demoapp.core.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resumeWithException

class WifiSessionManager(
    private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    private val wifiManager = context.applicationContext.getSystemService(WifiManager::class.java)

    // ============== At least API 29 ==============
    // 单次请求
    private var requestCallback: ConnectivityManager.NetworkCallback? = null
    private var currentNetwork: Network? = null

    // ============== Below API 29 ===============
    private var previousNetworkId: Int? = null

    private var previousSsid: String? = null

    private val _state = MutableStateFlow<WifiState>(WifiState.Idle)
    val state = _state.asStateFlow()

    val isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled

    // 用于监听全局的变化
    private val defaultCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.i(TAG, "default --> onAvailable: ${network}")
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                Log.i(TAG, "default --> onBlockedStatusChanged: ${blocked}")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.i(TAG, "default --> onCapabilitiesChanged: ${networkCapabilities}")
            }

            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
            ) {
                Log.i(TAG, "default --> onLinkPropertiesChanged: ${linkProperties}")
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                Log.i(TAG, "default --> onLosing: ${maxMsToLive}")
            }

            override fun onLost(network: Network) {
                Log.i(TAG, "default --> onLost: ${network}")
            }

            override fun onReserved(networkCapabilities: NetworkCapabilities) {
                Log.i(TAG, "default --> onReserved: ${networkCapabilities}")
            }

            override fun onUnavailable() {
                Log.i(TAG, "default --> onUnavailable")
            }
        }
    }


    private val connectMutex = Mutex()

    init {
//        connectivityManager.registerDefaultNetworkCallback(defaultCallback)
        WifiMonitor.init(context)
    }

    val currentSsid = WifiMonitor.currentSsid

    fun connect(
        ssid: String,
        password: String,
    ) {
        val current = currentSsid()
        if (current == ssid) throw WifiException.AlreadyConnected()

        if (!isWifiEnabled) throw WifiException.Disabled()

        if (!isWifiEnabled) throw WifiException.Disabled()

        scope.launch {
            connectMutex.withLock {
                if (state.value != WifiState.Connecting) {
                    _state.value = WifiState.Connecting

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            connectTo(ssid, password)
                        } else {
                            connectLegacy(ssid, password)
                        }
                        _state.value = WifiState.Connected(ssid)
                    } catch (e: WifiException) {
                        _state.value = WifiState.Error(e)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun connectTo(
        ssid: String,
        password: String,
    ): Unit = suspendCancellableCoroutine { continuation ->
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

            requestCallback?.let { connectivityManager.unregisterNetworkCallback(it) }

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.i(TAG, "onAvailable: ${network}")

                    if (connectivityManager.bindProcessToNetwork(network)) {
                        WifiMonitor.updateCurrentSsidManually(network)
                        if (continuation.isActive) {
                            continuation.resume(Unit) { cause, value, _ ->
                                Log.w(TAG, "onAvailable & onCancel: $cause $value")
                            }
                        }
                    }
                }

                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    Log.i(TAG, "onBlockedStatusChanged: ${blocked}")
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    Log.i(TAG, "onCapabilitiesChanged: ${networkCapabilities}")
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    Log.i(TAG, "onLinkPropertiesChanged: ${linkProperties}")
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    Log.i(TAG, "onLosing: ${maxMsToLive}")
                }

                override fun onLost(network: Network) {
                    Log.i(TAG, "onLost: ${network}")
                }

                override fun onReserved(networkCapabilities: NetworkCapabilities) {
                    Log.i(TAG, "onReserved: ${networkCapabilities}")
                }

                override fun onUnavailable() {
                    Log.i(TAG, "onUnavailable")

                    if (continuation.isActive) {
                        continuation.resumeWithException(WifiException.Unavailable())
                    }
                }
            }
                .also { requestCallback = it }
            connectivityManager.requestNetwork(request, callback)
        } catch (e: Exception) {
            if (continuation.isActive) {
                val error = when (e) {
                    is IllegalArgumentException -> WifiException.Failed("request contains invalid network capabilities ==> $e")
                    is SecurityException -> WifiException.PermissionMissing()
                    is RuntimeException -> WifiException.Failed("the app already has too many callbacks registered ==> $e")
                    else -> WifiException.Failed("Unknown error ==> $e")
                }
                continuation.resumeWithException(error)
            }
        }

        continuation.invokeOnCancellation {
            requestCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
            requestCallback = null
        }

    }

    private suspend fun connectLegacy(
        ssid: String,
        password: String,
    ) {
        if (!wifiManager.isWifiEnabled && !wifiManager.setWifiEnabled(true)) {
            throw WifiException.Disabled()
        }

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

            delay(200L)

            if (!wifiManager.enableNetwork(networkId, true)) {
                // 此处attemptConnect为true的话，是否还需要reconnect
                throw WifiException.Failed()
            }

            delay(200L)

            if (!wifiManager.reconnect()) {
                throw WifiException.Failed()
            }

        }
    }

    fun observeWifiConnection() = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                    val info =
                        intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)

                    trySend(info)
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION
            )
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    fun disconnect() {
        requestCallback?.let {
            runCatching {
                connectivityManager.unregisterNetworkCallback(it)
            }
        }
        requestCallback = null

        currentNetwork = null

        connectivityManager.bindProcessToNetwork(null)

        _state.value = WifiState.Idle
    }

    fun currentSsid(): String? {
        val info = wifiManager.connectionInfo

        return info.ssid
            ?.removePrefix("\"")
            ?.removeSuffix("\"")
    }

    fun isConnectedTo(
        expected: String
    ): Boolean {
        return currentSsid() == expected
    }

    fun startMonitor() {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) {
                val wifi = caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI,
                )

                if (!wifi) {
                    _state.value = WifiState.Lost
                }
            }

            override fun onLost(network: Network) {
                _state.value = WifiState.Lost
            }
        }
            .also {
                requestCallback = it
            }
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    companion object {
        private const val TAG = "WifiSessionManager"
    }
}