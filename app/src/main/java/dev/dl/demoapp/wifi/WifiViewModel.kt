package dev.dl.demoapp.wifi

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.dl.demoapp.core.wifi.WifiConnector
import dev.dl.demoapp.core.wifi.WifiException
import dev.dl.demoapp.core.wifi.WifiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class WifiViewModel(
    context: Context,
) : ViewModel() {

    private val wifiConnector: WifiConnector = WifiConnector(context)
//
//    private val _uiState = MutableStateFlow<WifiScanUiState>(WifiScanUiState.Idle)
//    val scanUiState = _uiState.asStateFlow()
//
//    private var scanJob: Job? = null
//
//    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//    fun scan() {
//        scanJob?.cancel()
//
//        _uiState.value = WifiScanUiState.Scanning
//        scanJob = viewModelScope.launch {
//            try {
//                val result = wifiConnector.scan()
//
//                _uiState.value = WifiScanUiState.Success(result)
//            } catch (e: Exception) {
//                if (e is CancellationException) throw e
//
//                e.printStackTrace()
//                _uiState.value = WifiScanUiState.Failure(e)
//            }
//        }
//    }
//
//    fun stop() {
//        scanJob?.cancel()
//        scanJob = null
//        _uiState.value = WifiScanUiState.Idle
//    }

    private val _connectUiState = MutableStateFlow<WifiConnectUiState>(WifiConnectUiState.Idle)
    val connectUiState = _connectUiState.asStateFlow()

    init {
        viewModelScope.launch {
            WifiSessionManager.connectState
                .collectLatest {
                    Log.i(TAG, "wifi state: $it")
                    when (it) {
                        is WifiState.Connected -> {
                            if (it.ssid == SSID) {

                                _connectUiState.value = WifiConnectUiState.Connected(it.ssid)
                            } else {
                                _connectUiState.value = WifiConnectUiState.Idle
                            }
                        }

                        is WifiState.ConnectedFailed -> {
                            handleWifiError(it.error)
                        }

                        WifiState.Connecting -> {
                            _connectUiState.value = WifiConnectUiState.Connecting
                        }

                        WifiState.Idle -> {}
                        WifiState.Lost -> {
                            // TODO Connection lost
                            _connectUiState.value = WifiConnectUiState.Idle
                        }
                    }
                }
        }
    }

    private val _uiState = MutableStateFlow(WifiUiState())
    val uiState = _uiState.asStateFlow()

    fun updateSsid(value: String) {
        _uiState.update {
            it.copy(ssid = value)
        }
    }

    fun updatePassword(value: String) {
        _uiState.update {
            it.copy(password = value)
        }
    }


    private fun handleWifiError(error: WifiException) {
        _connectUiState.value = WifiConnectUiState.ConnectFailed(error)
//        when(error) {
//            is WifiException.WifiDisabled -> {
//                _connectUiState.value = WifiConnectUiState.Failed()
//            }
//            is WifiException.Failed -> TODO()
//            is WifiException.LocationDisabled -> TODO()
//            is WifiException.PermissionMissing -> TODO()
//            is WifiException.Unavailable -> TODO()
//        }
    }

    fun checkStatus(context: Context) {
        try {
            WifiSessionManager.checkStatus(context)
        } catch (e: WifiException) {
            _connectUiState.value = WifiConnectUiState.CheckFailed(e)
        }
    }

    fun checkStatusAgain(context: Context) {
        try {
            WifiSessionManager.checkStatus(context)
        } catch (e: WifiException) {
            _connectUiState.value = WifiConnectUiState.CheckFailed(e, true)
        }
    }

    fun connectWifi() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = uiState.value
            val ssid = state.ssid.trim()
            val password = state.password.trim()
            WifiSessionManager.connectTo(ssid, password)
        }
    }

    fun disconnect() {
        WifiSessionManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()

        WifiSessionManager.cleanup()
    }

    companion object {
        private const val TAG = "WifiViewModel"
        private const val SSID = "Device_Test"
        private const val PASSWORD = "12345678"
        private const val PASSWORD2 = "87654321"
//        private const val SSID = "iPhone"
//        private const val PASSWORD = "qwertyui"
    }
}

internal class WifiViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WifiViewModel(context) as T
    }
}

internal sealed interface WifiScanUiState {
    data object Idle : WifiScanUiState
    data object Scanning : WifiScanUiState
    data class Success(
        val result: List<String>,
    ) : WifiScanUiState

    data class Failure(
        val error: Throwable
    ) : WifiScanUiState
}

internal sealed interface WifiConnectUiState {
    data object Idle : WifiConnectUiState
    data object Connecting : WifiConnectUiState
    data class Connected(
        val ssid: String,
    ) : WifiConnectUiState

    data class ConnectFailed(
        val error: WifiException,
    ) : WifiConnectUiState

    data class CheckFailed(
        val error: WifiException,
        val again: Boolean = false,
    ) : WifiConnectUiState
}

internal data class WifiUiState(
    val ssid: String = "",
    val password: String = ""
)