package com.toriigoogleremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.toriigoogleremote.ui.MainScreen
import com.toriigoogleremote.ui.theme.ToriiGoogleRemoteTheme

/**
 * Activity tipis: hanya membuat RemoteConfigManager lalu menayangkan UI Compose.
 * Seluruh logika tampilan ada di [MainScreen] (Jetpack Compose).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val remoteConfigManager = RemoteConfigManager(this)

        setContent {
            ToriiGoogleRemoteTheme {
                MainScreen(manager = remoteConfigManager)
            }
        }
    }
}
