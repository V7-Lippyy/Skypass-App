# SkyPass

SkyPass adalah aplikasi Android yang menyediakan informasi cuaca dan kompas digital dalam satu platform terintegrasi.

## Fitur Utama

1. **Informasi Cuaca Real-time**: Data cuaca terkini berdasarkan lokasi pengguna
2. **Prakiraan 3 Hari**: Visualisasi prakiraan cuaca untuk tiga hari ke depan
3. **Kompas Digital**: Navigasi dengan kompas yang dilengkapi indikator arah yang jelas
4. **Pencarian Lokasi**: Melihat informasi cuaca dari lokasi manapun yang dicari
5. **UI Modern**: Antarmuka pengguna yang intuitif dengan Jetpack Compose
6. **Mode Offline**: Menyimpan data cuaca terakhir untuk diakses tanpa internet

## Teknologi

- Kotlin 1.5+
- Jetpack Compose 1.0+
- Arsitektur MVVM (Model-View-ViewModel)
- Retrofit & OkHttp untuk API calls
- Moshi untuk JSON parsing
- Dagger Hilt untuk dependency injection
- Coil untuk loading gambar
- Coroutines & Flow untuk operasi asinkron
- WeatherAPI.com untuk data cuaca
- Sensor hardware (accelerometer dan magnetometer)

## Persyaratan Sistem

- Android 5.0 (API 21) atau lebih tinggi
- 2GB RAM minimal
- 100MB ruang penyimpanan
- Sensor accelerometer dan magnetometer untuk fitur kompas
- Akses internet untuk data cuaca terbaru

## Instalasi

1. Clone repository
```
git clone https://github.com/your-username/skypass.git
```

2. Buka project di Android Studio

3. Tambahkan API key dari WeatherAPI.com di `Constants.kt`
```kotlin
const val WEATHER_API_KEY = "your-api-key-here"
```

4. Build dan jalankan aplikasi

## Struktur Proyek

```
com.example.skypass/
├── data/               # Data layer (model, remote, repository)
├── di/                 # Dependency injection modules
├── ui/                 # UI components dan screens 
│   ├── compass/        # Kompas screen dan viewmodel
│   ├── home/           # Home screen
│   ├── navigation/     # Navigation components
│   ├── splash/         # Splash screen
│   ├── weather/        # Weather screen dan viewmodel
│   └── theme/          # App styling (colors, typography)
└── util/               # Utilities, constants, extensions
```

## Database

SkyPass menggunakan Room Database untuk penyimpanan lokal dengan entity:
- **WeatherEntity**: Menyimpan data cuaca saat ini
- **ForecastEntity**: Menyimpan data prakiraan cuaca
- **LocationEntity**: Menyimpan riwayat lokasi yang dicari

## Pengembang

© 2025 Muhammad Alif Qadri

## Lisensi

[MIT License](LICENSE)
