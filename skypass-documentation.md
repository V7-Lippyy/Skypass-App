# Dokumentasi Proyek: Android Kotlin Jetpack Compose MVVM

## 1. Judul dan Deskripsi Proyek

**Nama Proyek**: SkyPass

**Deskripsi Proyek**:
SkyPass adalah aplikasi mobile berbasis Android yang menyediakan informasi cuaca dan kompas digital dalam satu platform. Aplikasi ini menggunakan Jetpack Compose untuk antarmuka pengguna modern dan arsitektur MVVM untuk pemisahan yang bersih antara logika bisnis dan tampilan. Target pengguna adalah orang-orang yang membutuhkan informasi cuaca akurat dan alat navigasi sederhana seperti kompas dalam aktivitas sehari-hari. Fitur utama yang disediakan adalah informasi cuaca real-time, prakiraan 3 hari, dan kompas digital dengan indikator arah yang jelas.

## 2. Persyaratan Sistem (System Requirements)

**Perangkat Keras**:
- Minimum 2 GB RAM
- Minimal ruang penyimpanan 100 MB
- Sensor accelerometer dan magnetometer (untuk fitur kompas)

**Perangkat Lunak**:
- Android Studio (versi terbaru)
- JDK 8 atau lebih tinggi
- SDK Android API Level 21 (Lollipop) atau lebih tinggi
- Kotlin 1.5 atau lebih tinggi
- Jetpack Compose 1.0 atau lebih tinggi

**Dependensi Utama**:
- androidx.compose.ui:ui
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.activity:activity-compose
- androidx.compose.material3:material3
- androidx.navigation:navigation-compose
- androidx.hilt:hilt-navigation-compose
- com.squareup.retrofit2:retrofit
- com.squareup.moshi:moshi-kotlin
- com.squareup.okhttp3:okhttp
- coil-compose (untuk loading gambar)
- Dagger Hilt (untuk dependency injection)

## 3. Instalasi dan Konfigurasi

### Langkah 1: Mengunduh dan Menginstal Dependensi

1. Pastikan Android Studio telah terpasang.
2. Di dalam proyek Android, buka file build.gradle (project level) dan pastikan memiliki repository google() dan mavenCentral().
3. Di dalam file build.gradle (module level), tambahkan dependensi berikut:

```gradle
dependencies {
    // Core Android dan Jetpack
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.9.0'
    
    // Compose
    implementation platform('androidx.compose:compose-bom:2023.08.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.7'
    
    // Hilt untuk Dependency Injection
    implementation 'com.google.dagger:hilt-android:2.48'
    kapt 'com.google.dagger:hilt-android-compiler:2.48'
    implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
    
    // Retrofit & OkHttp untuk networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-moshi:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'
    
    // Moshi untuk JSON parsing
    implementation 'com.squareup.moshi:moshi-kotlin:1.13.0'
    kapt 'com.squareup.moshi:moshi-kotlin-codegen:1.13.0'
    
    // Coil untuk loading gambar
    implementation 'io.coil-kt:coil-compose:2.2.2'
}
```

### Langkah 2: Pengaturan File Konfigurasi

- **File AndroidManifest.xml**: Telah dikonfigurasi dengan izin yang diperlukan:
  - `INTERNET` dan `ACCESS_NETWORK_STATE` untuk akses data cuaca
  - `ACCESS_FINE_LOCATION` dan `ACCESS_COARSE_LOCATION` untuk mendapatkan lokasi pengguna
  - `POST_NOTIFICATIONS` untuk notifikasi
  - Penggunaan sensor accelerometer dan magnetometer untuk kompas

- **File Constants.kt**: Berisi konfigurasi API key dan base URL untuk Weather API.

### Langkah 3: Menyiapkan Lingkungan Pengembangan

1. Buka Android Studio.
2. Clone repository atau buka project SkyPass.
3. Sync project dengan Gradle files.
4. Pastikan emulator atau perangkat fisik memiliki sensor untuk fitur kompas.

## 4. Struktur Direktori (Directory Structure)

```
com.example.skypass/
├── data/
│   ├── model/
│   │   ├── ForecastModel.kt
│   │   ├── GeocodingModel.kt
│   │   └── WeatherModel.kt
│   ├── remote/
│   │   ├── GeocodingApiService.kt
│   │   └── WeatherApiService.kt
│   └── repository/
│       ├── LocationRepository.kt
│       └── WeatherRepository.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── compass/
│   │   ├── CompassScreen.kt
│   │   └── CompassViewModel.kt
│   ├── home/
│   │   ├── HomeFeatureCard.kt
│   │   └── HomeScreen.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── splash/
│   │   └── SplashScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Shape.kt
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   ├── weather/
│   │   ├── LocationSearchModal.kt
│   │   ├── WeatherScreen.kt
│   │   └── WeatherViewModel.kt
│   └── MainActivity.kt
├── util/
│   ├── Constants.kt
│   ├── Extensions.kt
│   ├── NetworkMonitor.kt
│   └── NetworkResult.kt
└── SkyPassApplication.kt
```

## 5. Penjelasan Tentang Kode Sumber (Source Code Explanation)

### File Utama:

- **SkyPassApplication.kt**: Aplikasi utama yang mengimplementasikan Hilt untuk dependency injection.

- **MainActivity.kt**: Activity utama yang menginisialisasi Compose dan memulai navigasi aplikasi.

- **NavGraph.kt**: Menentukan semua rute navigasi aplikasi termasuk Splash, Home, Weather, dan Compass.

- **WeatherViewModel.kt**: ViewModel untuk mengelola data cuaca dan interaksi dengan repository. Menangani permintaan izin lokasi, pencarian lokasi, dan pemrosesan data cuaca.

- **CompassViewModel.kt**: ViewModel untuk fitur kompas yang mengelola sensor accelerometer dan magnetometer untuk mendapatkan arah kompas.

- **WeatherRepository.kt**: Repository yang berinteraksi dengan WeatherAPI untuk mendapatkan data cuaca saat ini dan prakiraan.

- **LocationRepository.kt**: Repository untuk mengakses layanan lokasi perangkat untuk mendapatkan koordinat pengguna.

### Penjelasan Logika Kode:

#### Arsitektur MVVM:
- **Model**: Data class dalam package `data/model` seperti `WeatherResponse`, `ForecastResponse`, dan `GeocodingResponse`.
- **View**: Composable functions seperti `WeatherScreen`, `CompassScreen`, dan `HomeScreen`.
- **ViewModel**: Kelas seperti `WeatherViewModel` dan `CompassViewModel` yang mengelola logika bisnis dan state UI.

#### Weather Feature:
1. Aplikasi meminta izin lokasi pengguna melalui `WeatherViewModel.requestLocationPermission()`
2. Setelah mendapatkan izin, aplikasi mengambil lokasi pengguna melalui `LocationRepository.getCurrentLocation()`
3. Lokasi (latitude, longitude) digunakan untuk mengambil data cuaca dari WeatherAPI melalui `WeatherRepository`
4. Data cuaca ditampilkan di UI dengan Compose, menunjukkan kondisi saat ini dan prakiraan 3 hari

#### Compass Feature:
1. `CompassViewModel` mendaftarkan listener untuk sensor accelerometer dan magnetometer
2. Sensor data diolah untuk menghitung arah kompas dalam derajat
3. UI menampilkan arah dengan animasi rotasi dan indikator akurasi sensor
4. Pengguna dapat mengkalibrasi kompas dengan tombol "Recalibrate"

## 6. Penggunaan API

### Weather API (weatherapi.com):

- **Base URL**: https://api.weatherapi.com/v1/

- **Endpoint API untuk cuaca**:
  - GET /current.json: Mendapatkan kondisi cuaca saat ini
  - GET /forecast.json: Mendapatkan prakiraan cuaca

- **Parameter Kunci**:
  - key: API key untuk otentikasi
  - q: Query lokasi (bisa berupa nama kota atau koordinat "lat,lon")
  - days: Jumlah hari prakiraan (1-3)

- **Contoh Penggunaan**:
```kotlin
interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("aqi") includeAirQuality: String = "no"
    ): WeatherResponse

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 3,
        @Query("aqi") includeAirQuality: String = "no",
        @Query("alerts") includeAlerts: String = "no"
    ): ForecastResponse
}
```

## 7. Contoh Penggunaan (Usage Examples)

### Menjalankan Aplikasi:
1. Clone repository SkyPass ke mesin lokal.
2. Buka dengan Android Studio.
3. Pastikan API key untuk WeatherAPI sudah dikonfigurasi di `Constants.kt`.
4. Jalankan aplikasi dengan memilih emulator atau perangkat fisik.

### Menggunakan Fitur Cuaca:
```kotlin
// Contoh mendapatkan cuaca berdasarkan lokasi saat ini
fun loadWeatherData() {
    viewModelScope.launch {
        locationRepository.getCurrentLocation().collect { locationResult ->
            locationResult.fold(
                onSuccess = { location ->
                    val query = "${location.latitude},${location.longitude}"
                    fetchWeatherData(query)
                },
                onFailure = { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Failed to get location")
                }
            )
        }
    }
}

// Contoh mencari cuaca berdasarkan nama kota
fun searchLocation(locationName: String) {
    viewModelScope.launch {
        _uiState.value = WeatherUiState.Loading
        try {
            fetchWeatherData(locationName)
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error("Error searching location: ${e.message}")
        }
    }
}
```

### Menggunakan Fitur Kompas:
```kotlin
// Inisialisasi compass sensors
private fun checkSensors() {
    if (accelerometer == null || magnetometer == null) {
        _compassState.value = CompassState.SensorsNotAvailable
    } else {
        _compassState.value = CompassState.Active
    }
}

// Mendengarkan perubahan sensor
fun startListening() {
    if (_compassState.value == CompassState.Active) {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        magnetometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }
}

// Pemrosesan data sensor kompas
override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
        gravity = event.values.clone()
    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
        geomagnetic = event.values.clone()
    }

    if (gravity != null && geomagnetic != null) {
        val r = FloatArray(9)
        val i = FloatArray(9)

        if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(r, orientation)

            // Convert radians to degrees
            val degrees = (Math.toDegrees(orientation[0].toDouble()) + 360) % 360
            _compassDirection.value = degrees.toFloat()
        }
    }
}
```

## 8. Pengujian dan Test

### Jenis Pengujian yang Diperlukan:

- **Unit Test**:
  - ViewModel test untuk WeatherViewModel dan CompassViewModel
  - Repository test untuk WeatherRepository dan LocationRepository
  
- **UI Test**:
  - Test navigasi antar layar
  - Test UI pada WeatherScreen dan CompassScreen

### Menjalankan Pengujian:
1. Di Android Studio, buka tab Run > Edit Configurations
2. Tambahkan konfigurasi Android Tests
3. Pilih module app dan test artifact 'Android Instrumented Tests'
4. Run pengujian dengan klik kanan pada direktori test dan pilih 'Run Tests'

### Skenario pengujian:
- Verifikasi bahwa API cuaca memberikan respons yang benar
- Verifikasi pembacaan sensor kompas bekerja dengan baik
- Verifikasi navigasi antar layar berfungsi seperti yang diharapkan
- Verifikasi UI menampilkan data yang benar

## 9. Troubleshooting dan Pemecahan Masalah

### Masalah Umum:

1. **Kompas Tidak Berfungsi**:
   - **Masalah**: Pada beberapa perangkat, kompas mungkin tidak berfungsi atau tidak akurat.
   - **Solusi**: Pastikan perangkat memiliki sensor accelerometer dan magnetometer. Gunakan metode kalibrasi dengan gerakan pola angka 8.

2. **Lokasi Tidak Ditemukan**:
   - **Masalah**: Aplikasi tidak dapat menemukan lokasi pengguna.
   - **Solusi**: Pastikan izin lokasi telah diberikan dan GPS diaktifkan. Periksa `LocationRepository` untuk memastikan fungsi `getCurrentLocation()` bekerja dengan benar.

3. **Data Cuaca Tidak Tampil**:
   - **Masalah**: Aplikasi tidak menampilkan data cuaca.
   - **Solusi**: Periksa koneksi internet dan API key di `Constants.kt`. Pastikan query lokasi valid. Gunakan Logcat untuk memeriksa respon API.

4. **Crash saat Startup**:
   - **Masalah**: Aplikasi crash saat pertama kali dibuka.
   - **Solusi**: Pastikan semua dependensi telah diimpor dengan benar. Periksa `SplashScreen` dan `MainActivity` untuk kesalahan inisialisasi.