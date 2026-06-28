package dev.dl.demoapp.wifi

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.dl.demoapp.core.designsystem.theme.AppTheme
import dev.dl.demoapp.core.wifi.WifiException

@Composable
fun WifiApp() {
    AppTheme {
        Scaffold { innerPadding ->
            val context = LocalContext.current
            val viewModel: WifiViewModel = viewModel(
                factory = WifiViewModelFactory(context.applicationContext)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                WifiScanView(viewModel)

                Spacer(modifier = Modifier.height(10.dp))

                WifiConnectionView(viewModel)
            }
        }
    }
}

@Composable
private fun WifiScanView(viewModel: WifiViewModel) {
    val uiState by viewModel.scanUiState.collectAsStateWithLifecycle()
    val isScanning = uiState == WifiScanUiState.Scanning

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedMap ->
        val locationGranted = grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val wifiGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            grantedMap[Manifest.permission.NEARBY_WIFI_DEVICES] == true
        } else {
            true
        }
        if (wifiGranted && locationGranted) {
//            viewModel.scan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = if (uiState is WifiScanUiState.Failure) "Error: $uiState" else "No Error",
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Button(onClick = {
            if (isScanning) {
                viewModel.stop()
            } else {
                launcher.launch(requiredScanPermissions())
            }
        }) {
            Text(if (isScanning) "Stop" else "Scan")
        }
        if (uiState is WifiScanUiState.Success) {
            val scanResult = (uiState as WifiScanUiState.Success).result
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(scanResult, key = { it }) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
private fun WifiConnectionView(viewModel: WifiViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val context = LocalContext.current

        val state by viewModel.connectUiState.collectAsStateWithLifecycle()
        val canConnect =
            state == WifiConnectUiState.Idle || state is WifiConnectUiState.ConnectFailed
        val canDisconnect = state is WifiConnectUiState.Connected

        val stateStr = when (state) {
            is WifiConnectUiState.Connected -> "Connected: ${(state as WifiConnectUiState.Connected).ssid}"
            WifiConnectUiState.Connecting -> "Connecting"
            is WifiConnectUiState.ConnectFailed -> "Failed to connect: ${(state as WifiConnectUiState.ConnectFailed).error}"
            WifiConnectUiState.Idle -> "Idle"
            is WifiConnectUiState.CheckFailed -> "Check failed"
        }
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantedMap ->
            val granted =
                grantedMap[Manifest.permission.CHANGE_NETWORK_STATE] == true && grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (granted) {
                viewModel.connectWifi()
            }
        }

        val permissionLauncher2 = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantedMap ->
            val granted =
                grantedMap[Manifest.permission.CHANGE_NETWORK_STATE] == true && grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (granted) {
                viewModel.checkStatusAgain(context)
            } else {
                // TODO If the user refused to do it, dont ask again
            }
        }

        val wifiPanelLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel.connectWifi()
        }

        val locationPanelLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel.connectWifi()
        }

        val locationPanelLauncher2 = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel.checkStatusAgain(context)
        }


        Text(
            text = "Connection State: $stateStr",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            style = TextStyle(
                fontSize = 26.sp,
            )
        )

        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(Unit, lifecycleOwner) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectUiState.collect {
                    if (it is WifiConnectUiState.ConnectFailed) {
                        when (it.error) {
                            is WifiException.WifiDisabled -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    wifiPanelLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
                                } else {
                                    wifiPanelLauncher.launch(Intent(Settings.ACTION_SETTINGS))
                                }
                            }

                            is WifiException.LocationDisabled -> {
                                // For Google service available
//                            val request = LocationSettingsRequest.Builder()
//                                .addLocationRequest(locationRequest)
//                                .build()
//
//                            LocationServices.getSettingsClient(context)
//                                .checkLocationSettings(request)
//                                .addOnFailureListener { exception ->
//                                    if (exception is ResolvableApiException) {
//                                        exception.startResolutionForResult(activity, REQUEST_CODE)
//                                    }
//                                }
                                locationPanelLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }

                            is WifiException.Failed -> TODO()
                            is WifiException.PermissionMissing -> {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.CHANGE_NETWORK_STATE,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                    )
                                )
                            }

                            is WifiException.Unavailable -> {

                            }
                        }
                    } else if (it is WifiConnectUiState.CheckFailed) {
                        when (it.error) {
//                            is WifiException.WifiDisabled -> {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                    wifiPanelLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
//                                } else {
//                                    wifiPanelLauncher.launch(Intent(Settings.ACTION_SETTINGS))
//                                }
//                            }

                            is WifiException.LocationDisabled -> {
                                // For Google service available
//                            val request = LocationSettingsRequest.Builder()
//                                .addLocationRequest(locationRequest)
//                                .build()
//
//                            LocationServices.getSettingsClient(context)
//                                .checkLocationSettings(request)
//                                .addOnFailureListener { exception ->
//                                    if (exception is ResolvableApiException) {
//                                        exception.startResolutionForResult(activity, REQUEST_CODE)
//                                    }
//                                }
                                if (it.again) {
                                    // TODO If the user refused to do it, dont ask again
                                } else {
                                    locationPanelLauncher2.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                }
                            }

                            is WifiException.PermissionMissing -> {
                                if (it.again) {
                                    // TODO If the user refused to do it, dont ask again
                                } else {
                                    permissionLauncher2.launch(
                                        arrayOf(
                                            Manifest.permission.CHANGE_NETWORK_STATE,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                        )
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
        LaunchedEffect(Unit, lifecycleOwner) {
            viewModel.checkStatus(context)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    permissionLauncher.launch(requiredConnectPermissions())
                },
                enabled = canConnect,
            ) {
                Text("Connect")
            }
            Button(
                onClick = {
                    viewModel.disconnect()
                },
                enabled = canDisconnect,
            ) {
                Text("Disconnect")
            }
        }
//
//        val currentSsid by viewModel.currentWifi.collectAsStateWithLifecycle()
//        Text("Current: $currentSsid")
    }
}

private fun requiredScanPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES,
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}

private fun requiredConnectPermissions(): Array<String> {
    return arrayOf(
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
}