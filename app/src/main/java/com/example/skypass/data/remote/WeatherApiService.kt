package com.example.skypass.data.remote

import com.example.skypass.data.model.ForecastResponse
import com.example.skypass.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String, // Can be lat,lon or city name
        @Query("aqi") includeAirQuality: String = "no"
    ): WeatherResponse

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String, // Can be lat,lon or city name
        @Query("days") days: Int = 3, // Number of forecast days (1-3)
        @Query("aqi") includeAirQuality: String = "no",
        @Query("alerts") includeAlerts: String = "no"
    ): ForecastResponse
}