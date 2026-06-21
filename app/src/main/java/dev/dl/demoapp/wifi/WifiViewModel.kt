package dev.dl.demoapp.wifi

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.dl.demoapp.core.wifi.WifiConnector
import dev.dl.demoapp.core.wifi.WifiException
import dev.dl.demoapp.core.wifi.WifiSessionManager
import dev.dl.demoapp.core.wifi.WifiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WifiViewModel(
    context: Context,
) : ViewModel() {

    private val wifiConnector: WifiConnector = WifiConnector(context)

    private val _uiState = MutableStateFlow<WifiScanUiState>(WifiScanUiState.Idle)
    val scanUiState = _uiState.asStateFlow()

    private var scanJob: Job? = null

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun scan() {
        scanJob?.cancel()

        _uiState.value = WifiScanUiState.Scanning
        scanJob = viewModelScope.launch {
            try {
                val result = wifiConnector.scan()

                _uiState.value = WifiScanUiState.Success(result)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                e.printStackTrace()
                _uiState.value = WifiScanUiState.Failure(e)
            }
        }
    }

    fun stop() {
        scanJob?.cancel()
        scanJob = null
        _uiState.value = WifiScanUiState.Idle
    }

    private val _connectUiState = MutableStateFlow<WifiConnectUiState>(WifiConnectUiState.Idle)
    val connectUiState = _connectUiState.asStateFlow()
    private val wifiSessionManager = WifiSessionManager(context)

    private val _connectErrorEvent = MutableSharedFlow<WifiException>()

    val connectErrorEvent = _connectErrorEvent.asSharedFlow()

    private var observeConnectStateJob: Job? = null

    val currentWifi = wifiSessionManager.currentSsid

    fun connectWifi() {
        observeConnectStateJob?.cancel()

        observeConnectStateJob = viewModelScope.launch {
            wifiSessionManager.state
                .collect {
                    when (it) {
                        WifiState.Idle ->
                            _connectUiState.value = WifiConnectUiState.Idle

                        WifiState.Connecting ->
                            _connectUiState.value = WifiConnectUiState.Connecting

                        is WifiState.Connected -> _connectUiState.value =
                            WifiConnectUiState.Connected

                        is WifiState.Error ->
                            _connectUiState.value =
                                WifiConnectUiState.Failed(IllegalStateException(it.error.toString()))

                        WifiState.Lost -> _connectUiState.value =
                            WifiConnectUiState.Failed(IllegalStateException("Lost!!"))
                    }
                }
        }

        wifiSessionManager.connect(SSID, PASSWORD)
    }

    fun disconnect() {
        wifiSessionManager.disconnect()
    }

    fun onBackFromWifiSettings() {
        if (wifiSessionManager.isWifiEnabled) {
            connectWifi()
        } else {

        }
    }

    private fun observeConnectState() {
    }

    companion object {
        private const val SSID = "dl wireless"
        private const val PASSWORD = "zxkj123456"
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
    data object Connected : WifiConnectUiState
    data class Failed(
        val error: Throwable,
    ) : WifiConnectUiState
}