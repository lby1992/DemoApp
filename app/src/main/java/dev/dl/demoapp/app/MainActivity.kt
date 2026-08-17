package dev.dl.demoapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.dl.demoapp.wifi.WifiApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
//        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
//
//        splash.setKeepOnScreenCondition { viewModel.state == AppState.Loading }
//        splash.setOnExitAnimationListener { splashView ->
//            splashView.view.animate()
//                .alpha(0f)
//                .setDuration(300L)
//                .withEndAction {
//                    splashView.remove()
//                }
//                .start()
//        }

        setContent {
//            AppRoot()
            WifiApp()
        }
    }
}