package com.toriigoogleremote.ui

import android.graphics.Color
import com.toriigoogleremote.RemoteConfigManager
import com.toriigoogleremote.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.toriigoogleremote.ui.theme.ToriiGoogleRemoteTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * State UI yang digambar dari nilai Remote Config.
 * Setiap fetch selesai, state ini di-update dan Compose merender ulang secara otomatis.
 */
data class RemoteConfigUiState(
    val welcomeMessage: String = "",
    val bannerVisible: Boolean = false,
    val bannerColor: ComposeColor = ComposeColor.Gray,
    val maxItems: Int = 0,
    val lastSync: String = ""
)

/**
 * Layar utama berbasis Jetpack Compose (pengganti activity_main.xml + ViewBinding).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(manager: RemoteConfigManager) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var uiState by remember { mutableStateOf(readState(manager)) }
    var isFetching by remember { mutableStateOf(false) }
    // Naik setiap fetch selesai -> memicu LaunchedEffect untuk menampilkan Snackbar.
    var fetchTick by remember { mutableIntStateOf(0) }
    var lastFetchSucceeded by remember { mutableStateOf(true) }

    /** Jalankan fetch di background, lalu baca ulang config & render ulang UI. */
    suspend fun runFetch() {
        if (isFetching) return
        isFetching = true
        lastFetchSucceeded = manager.fetchAndActivate()
        uiState = readState(manager)
        isFetching = false
        fetchTick++
    }

    // Fetch pertama saat layar dibuka (non-blocking, di Dispatchers.IO oleh firebase-ktx).
    LaunchedEffect(Unit) { runFetch() }

    // Snackbar hasil fetch (dipicu setelah fetch manual/otomatis selesai).
    LaunchedEffect(fetchTick) {
        if (fetchTick > 0) {
            val message = if (lastFetchSucceeded) {
                "Remote Config berhasil di-update dari server ✅"
            } else {
                "Gagal fetch — memakai nilai default/cache ⚠️"
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_remote_config_demo)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---- Kartu pesan sambutan (welcome_message) ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = uiState.welcomeMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // ---- Banner (show_banner + banner_color) ----
            if (uiState.bannerVisible) {
                RemoteBanner(color = uiState.bannerColor)
            }

            // ---- Info max_items & status sinkronisasi ----
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.max_items_label, uiState.maxItems),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = uiState.lastSync.ifEmpty {
                            stringResource(R.string.default_status)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ---- Tombol fetch manual ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { scope.launch { runFetch() } },
                    enabled = !isFetching
                ) {
                    Text(stringResource(R.string.btn_fetch_remote_config))
                }
                if (isFetching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

/** Banner warna penuh yang warnanya dikendalikan Remote Config (hex string). */
@Composable
private fun RemoteBanner(color: ComposeColor) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.banner_text),
            color = contrastColor(color),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

/** Pilih teks hitam/putih agar terbaca di atas warna banner apa pun. */
private fun contrastColor(bg: ComposeColor): ComposeColor {
    val luminance = 0.299 * bg.red + 0.587 * bg.green + 0.114 * bg.blue
    return if (luminance > 0.5) ComposeColor.Black else ComposeColor.White
}

/** Baca semua nilai aktif dari RemoteConfigManager menjadi state UI. */
private fun readState(manager: RemoteConfigManager): RemoteConfigUiState {
    val hex = manager.getBannerColor()
    val bannerColor = try {
        ComposeColor(Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        // Warna tidak valid dari console -> fallback netral.
        ComposeColor(0xFF607D8B)
    }
    return RemoteConfigUiState(
        welcomeMessage = manager.getString(RemoteConfigManager.WELCOME_MESSAGE),
        bannerVisible = manager.isBannerVisible(),
        bannerColor = bannerColor,
        maxItems = manager.getMaxItems(),
        lastSync = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            .let { "Terakhir disinkronkan: $it" }
    )
}

// ---------- Previews (Android Studio) ----------

@Preview(showBackground = true)
@Composable
private fun RemoteBannerPreview() {
    ToriiGoogleRemoteTheme {
        RemoteBanner(color = ComposeColor(0xFFFF9800))
    }
}
