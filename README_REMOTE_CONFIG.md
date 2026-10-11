# Torii Google Remote — Demo Firebase Remote Config (Kotlin)

Aplikasi Android sederhana yang mengimplementasikan **Firebase Remote Config** menggunakan Kotlin dan build system `*.gradle.kts` (Kotlin DSL + version catalog).

## Fitur Demo

UI menampilkan beberapa elemen yang dikendalikan dari server Remote Config:

| Key Remote Config | Tipe         | Fungsi                                   |
|-------------------|--------------|------------------------------------------|
| `welcome_message` | String       | Teks pesan sambutan pada banner          |
| `show_banner`     | Boolean      | Menampilkan / menyembunyikan banner      |
| `banner_color`    | String       | Warna banner (hex, mis. `#FF9800`)       |
| `max_items`       | Long/Number  | Angka maksimum item yang ditampilkan     |

Alur kerja:
1. Nilai **default** dibaca dari `res/xml/remote_config_defaults.xml` (aplikasi tetap jalan saat offline).
2. Tombol **Fetch Remote Config** memanggil `fetchAndActivate()` di background thread.
3. Jika sukses, UI di-render ulang dengan nilai dari server; jika gagal, nilai default/cache dipakai.
4. Saat `DEBUG`, `minimumFetchIntervalInSeconds = 0` agar mudah testing; di release 3600 detik.

## Struktur Penting

```
app/
├── google-services.json              # ⚠️ PLACEHOLDER — ganti dengan file asli!
├── build.gradle.kts                  # plugin google-services + firebase-config (BoM)
└── src/main/
    ├── AndroidManifest.xml           # izin INTERNET + launcher MainActivity
    ├── java/com/toriigoogleremote/
    │   ├── MainActivity.kt           # UI + fetch + render config
    │   └── RemoteConfigManager.kt    # pembungkus Firebase Remote Config
    └── res/
        ├── layout/activity_main.xml
        └── xml/remote_config_defaults.xml
gradle/libs.versions.toml             # versi AGP, Firebase BoM, config lib
```

## Cara Setup Firebase (Wajib)

1. Buka [Firebase Console](https://console.firebase.google.com) → **Add project**.
2. **Project settings → Your apps → Add Android app**
   - Package name: `com.toriigoogleremote`
3. Unduh **`google-services.json`** dan **timpa** file placeholder di `app/google-services.json`.
4. Di menu **Remote Config**, klik *Get started*, lalu tambahkan parameter:
   - `welcome_message` (String)
   - `show_banner` (Boolean)
   - `banner_color` (String)
   - `max_items` (Number)
   Isi nilainya, kemudian **Publish changes**.
5. Build & jalankan aplikasi, tekan tombol **Fetch Remote Config** — nilai dari server akan langsung tampil.

> Tips: untuk pengujian cepat, gunakan *Remote Config template* + Realtime mode (`fetchAndActivate` dengan cache expiration 0 sudah dikonfigurasi untuk debug build).

## Build

```bash
./gradlew assembleDebug
```

Prasyarat: JDK 17+, Android SDK (compileSdk 37 sesuai proyek).
