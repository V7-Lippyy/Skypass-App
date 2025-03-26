// ui/weather/WeatherViewModel.kt
package com.example.skypass.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skypass.data.model.ForecastResponse
import com.example.skypass.data.model.WeatherResponse
import com.example.skypass.data.repository.LocationRepository
import com.example.skypass.data.repository.WeatherRepository
import com.example.skypass.util.NetworkMonitor
import com.example.skypass.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Store last successful query for potential refresh
    private var lastSearchQuery: String? = null

    init {
        // Monitor network connectivity
        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { online ->
                _isOnline.value = online

                // If we're back online and have previous coordinates, refresh the data
                if (online && lastSearchQuery != null) {
                    fetchWeatherData(lastSearchQuery!!)
                }
            }
        }
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
        if (granted) {
            loadWeatherData()
        }
    }

    fun loadWeatherData() {
        if (!_isOnline.value) {
            _uiState.value = WeatherUiState.Error("No internet connection. Please check your network settings.")
            return
        }

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

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

    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        if (!_isOnline.value) {
            _uiState.value = WeatherUiState.Error("No internet connection. Please check your network settings.")
            return
        }

        viewModelScope.launch {
            val query = "$lat,$lon"
            fetchWeatherData(query)
        }
    }

    fun searchLocation(locationName: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                if (locationName.equals("current", ignoreCase = true)) {
                    // Use current location
                    loadWeatherData()
                    return@launch
                }

                if (!_isOnline.value) {
                    _uiState.value = WeatherUiState.Error("No internet connection. Please check your network settings.")
                    return@launch
                }

                // With WeatherAPI we can directly search by city name
                fetchWeatherData(locationName)

            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Error searching location: ${e.message}")
            }
        }
    }

    private suspend fun fetchWeatherData(query: String) {
        try {
            // Save query for potential refresh later
            lastSearchQuery = query

            when (val currentWeatherResult = weatherRepository.getCurrentWeather(query)) {
                is NetworkResult.Success -> {
                    when (val forecastResult = weatherRepository.getForecast(query)) {
                        is NetworkResult.Success -> {
                            val currentWeather = currentWeatherResult.data
                            val forecast = forecastResult.data
                            val dailyForecast = processForecast(forecast)

                            _uiState.value = WeatherUiState.Success(
                                currentWeather = currentWeather,
                                dailyForecast = dailyForecast
                            )
                        }
                        is NetworkResult.Error -> {
                            _uiState.value = WeatherUiState.Error(forecastResult.message)
                        }
                        is NetworkResult.Loading -> {
                            _uiState.value = WeatherUiState.Loading
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = WeatherUiState.Error(currentWeatherResult.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = WeatherUiState.Loading
                }
            }
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(e.message ?: "Failed to fetch weather data")
        }
    }

    // Add this to WeatherViewModel.kt
    fun requestLocationPermission() {
        // This function will be called when the user presses the permission request button
        // In a real implementation, you would request the permission from the system
        // For now, we'll just set a flag to simulate the process
        _locationPermissionGranted.value = true
        loadWeatherData()
    }

    private fun processForecast(forecastResponse: ForecastResponse): List<DailyForecast> {
        return forecastResponse.forecast.forecastday.map { forecastDay ->
            DailyForecast(
                date = forecastDay.date,
                maxTemp = forecastDay.day.maxTemp,
                minTemp = forecastDay.day.minTemp,
                condition = forecastDay.day.condition.text,
                icon = forecastDay.day.condition.icon
            )
        }
    }

    // Retry the last request
    fun retry() {
        if (lastSearchQuery != null) {
            viewModelScope.launch {
                fetchWeatherData(lastSearchQuery!!)
            }
        } else {
            loadWeatherData()
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val currentWeather: WeatherResponse,
        val dailyForecast: List<DailyForecast>
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

data class DailyForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val condition: String,
    val icon: String
)