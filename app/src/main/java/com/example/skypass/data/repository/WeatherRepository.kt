// data/repository/WeatherRepository.kt
package com.example.skypass.data.repository

import com.example.skypass.data.model.ForecastResponse
import com.example.skypass.data.model.WeatherResponse
import com.example.skypass.data.remote.WeatherApiService
import com.example.skypass.util.Constants
import com.example.skypass.util.NetworkResult
import com.example.skypass.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {
    suspend fun getCurrentWeather(query: String): NetworkResult<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                weatherApiService.getCurrentWeather(
                    apiKey = Constants.WEATHER_API_KEY,
                    query = query
                )
            }
        }
    }

    suspend fun getForecast(query: String, days: Int = 3): NetworkResult<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                weatherApiService.getForecast(
                    apiKey = Constants.WEATHER_API_KEY,
                    query = query,
                    days = days
                )
            }
        }
    }

    // Helper function for coordinates
    suspend fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): NetworkResult<WeatherResponse> {
        val query = "$lat,$lon"
        return getCurrentWeather(query)
    }

    suspend fun getForecastByCoordinates(lat: Double, lon: Double, days: Int = 3): NetworkResult<ForecastResponse> {
        val query = "$lat,$lon"
        return getForecast(query, days)
    }
}