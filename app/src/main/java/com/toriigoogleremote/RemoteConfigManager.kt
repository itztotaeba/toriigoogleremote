package com.toriigoogleremote

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.runBlocking

/**
 * Pembungkus sederhana untuk Firebase Remote Config.
 *
 * Alur kerja:
 * 1. Nilai default diambil dari res/xml/remote_config_defaults.xml (fallback lokal).
 * 2. Aplikasi melakukan fetch + activate ke server Firebase.
 * 3. Jika jaringan gagal, nilai default / cache terakhir yang dipakai.
 */
class RemoteConfigManager(context: Context) {

    companion object Keys {
        const val WELCOME_MESSAGE = "welcome_message"
        const val SHOW_BANNER = "show_banner"
        const val BANNER_COLOR = "banner_color"
        const val MAX_ITEMS = "max_items"
    }

    // Interval fetch: 0 detik saat debug agar mudah testing, 1 jam di produksi.
    private val isDebug = (context.applicationInfo.flags and
        android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val remoteConfig = Firebase.remoteConfig.apply {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (isDebug) 0 else 3600
        }
        setConfigSettingsAsync(configSettings)

        // Fallback value bila key belum ada di console Firebase.
        setDefaultsAsync(R.xml.remote_config_defaults)
    }

    /**
     * Mengambil konfigurasi terbaru dari server lalu mengaktifkannya.
     * @return true jika fetch & activate berhasil.
     */
    fun fetchAndActivate(): Boolean {
        return try {
            // API suspend di bungkus runBlocking agar mudah dipakai dari Activity sederhana.
            runBlocking { remoteConfig.fetchAndActivate() }
        } catch (e: Exception) {
            // Offline atau error lain -> tetap pakai nilai default/cache.
            false
        }
    }

    fun getString(key: String): String = remoteConfig[key].asString()

    fun getBoolean(key: String): Boolean = remoteConfig[key].asBoolean()

    fun getLong(key: String): Long = remoteConfig[key].asLong()

    /** Warna hex banner, mis. "#FF9800". */
    fun getBannerColor(): String = getString(BANNER_COLOR)

    fun isBannerVisible(): Boolean = getBoolean(SHOW_BANNER)

    fun getMaxItems(): Int = getLong(MAX_ITEMS).toInt()
}
