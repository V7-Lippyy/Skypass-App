package com.example.skypass.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather
)

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localtime: String
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "temp_c") val temp: Double,
    @Json(name = "feelslike_c") val feelsLike: Double,
    val humidity: Int,
    @Json(name = "wind_kph") val windSpeed: Double,
    @Json(name = "wind_degree") val windDeg: Int,
    val condition: WeatherCondition,
    @Json(name = "pressure_mb") val pressure: Double,
    @Json(name = "vis_km") val visibility: Double,
    @Json(name = "uv") val uvIndex: Double,
    @Json(name = "last_updated_epoch") val lastUpdated: Long
)

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)