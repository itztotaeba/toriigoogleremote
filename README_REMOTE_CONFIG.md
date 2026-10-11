# Torii Google Remote — Demo Firebase Remote Config (Kotlin + Jetpack Compose)

Aplikasi Android sederhana yang mengimplementasikan **Firebase Remote Config** menggunakan Kotlin, **Jetpack Compose (Material 3)**, dan build system `*.gradle.kts` (Kotlin DSL + version catalog).

## Fitur Demo

UI Compose menampilkan beberapa elemen yang dikendalikan dari server Remote Config:

| Key Remote Config | Tipe         | Fungsi                                   |
|-------------------|--------------|------------------------------------------|
| `welcome_message` | String       | Teks pesan sambutan pada kartu           |
| `show_banner`     | Boolean      | Menampilkan / menyembunyikan banner      |
| `banner_color`    | String       | Warna banner (hex, mis. `#FF9800`)       |
| `max_items`       | Long/Number  | Angka maksimum item yang ditampilkan     |

Alur kerja:
1. Nilai **default** dibaca dari `res/xml/remote_config_defaults.xml` (aplikasi tetap jalan saat offline).
2. Saat layar dibuka, `LaunchedEffect` otomatis memanggil `fetchAndActivate()` (suspend API — tanpa thread manual).
3. Tombol **Fetch Remote Config** memicu fetch ulang; hasil disimpan ke `State<RemoteConfigUiState>` sehingga Compose merender ulang secara deklaratif.
4. Hasil fetch dilaporkan lewat **M3 SnackbarHost**; warna banner dengan cerdas diberi teks hitam/putih (kontras luminance).
5. Saat `DEBUG`, `minimumFetchIntervalInSeconds = 0` agar mudah testing; di release 3600 detik.

## Struktur Penting

```
app/
├── google-services.json              # ⚠️ PLACEHOLDER — ganti dengan file asli!
├── build.gradle.kts                  # plugin compose-compiler + firebase-config (BoM)
└── src/main/
    ├── AndroidManifest.xml           # izin INTERNET + launcher MainActivity
    ├── java/com/toriigoogleremote/
    │   ├── MainActivity.kt           # ComponentActivity tipis -> setContent { MainScreen() }
    │   ├── RemoteConfigManager.kt    # pembungkus Firebase Remote Config (suspend fetch)
    │   ├── ui/MainScreen.kt          # seluruh UI Jetpack Compose + state + preview
    │   └── ui/theme/Theme.kt         # Material 3 theme (dynamic color Android 12+)
    └── res/
        └── xml/remote_config_defaults.xml
gradle/libs.versions.toml             # AGP, Compose BOM, Firebase BoM, compose compiler plugin
```

> Catatan revisi PR: `activity_main.xml`, ViewBinding, AppCompat & Material Views telah dihapus — tampilan kini 100% Jetpack Compose.

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
5. Build & jalankan aplikasi — fetch otomatis berjalan saat layar dibuka; tekan tombol **Fetch Remote Config** untuk refresh manual.

> Tips: untuk pengujian cepat, gunakan *Remote Config template* + Realtime mode (`fetchAndActivate` dengan cache expiration 0 sudah dikonfigurasi untuk debug build).

## Build

```bash
./gradlew assembleDebug
```

Prasyarat: JDK 17+, Android SDK (compileSdk 37 sesuai proyek).
