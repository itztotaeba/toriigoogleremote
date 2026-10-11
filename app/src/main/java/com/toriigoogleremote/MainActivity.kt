package com.toriigoogleremote

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.toriigoogleremote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        remoteConfigManager = RemoteConfigManager(this)

        // Tampilkan nilai default/cache dulu supaya UI langsung terisi.
        renderConfig()

        binding.btnRefresh.setOnClickListener { refreshRemoteConfig() }
    }

    private fun refreshRemoteConfig() {
        setViewVisible(binding.progressBar, true)
        binding.btnRefresh.isEnabled = false

        Thread {
            val updated = remoteConfigManager.fetchAndActivate()
            runOnUiThread {
                setViewVisible(binding.progressBar, false)
                binding.btnRefresh.isEnabled = true
                renderConfig()
                val message = if (updated) {
                    "Remote Config berhasil di-update dari server ✅"
                } else {
                    "Gagal fetch — memakai nilai default/cache ⚠️"
                }
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }.start()
    }

    /** Menggambar UI berdasarkan nilai Remote Config yang aktif. */
    private fun renderConfig() {
        binding.tvWelcomeMessage.text =
            remoteConfigManager.getString(RemoteConfigManager.WELCOME_MESSAGE)

        val bannerVisible = remoteConfigManager.isBannerVisible()
        setViewVisible(binding.bannerLayout, bannerVisible)
        if (bannerVisible) {
            val colorHex = remoteConfigManager.getBannerColor()
            try {
                binding.bannerLayout.setBackgroundColor(Color.parseColor(colorHex))
            } catch (e: IllegalArgumentException) {
                // Warna tidak valid dari console -> abaikan, pakai warna semula.
            }
        }

        binding.tvMaxItems.text =
            getString(R.string.max_items_label, remoteConfigManager.getMaxItems())

        binding.tvStatus.text = getString(
            R.string.status_last_fetch,
            java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
    }

    /** Helper agar set visibilitas lebih ringkas. */
    private fun setViewVisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}
