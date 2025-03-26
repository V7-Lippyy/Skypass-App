package com.example.skypass.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val location: Location,
    val current: CurrentWeather,
    val forecast: Forecast
)

@JsonClass(generateAdapter = true)
data class Forecast(
    val forecastday: List<ForecastDay>
)

@JsonClass(generateAdapter = true)
data class ForecastDay(
    val date: String,
    @Json(name = "date_epoch") val dateEpoch: Long,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

@JsonClass(generateAdapter = true)
data class Day(
    @Json(name = "maxtemp_c") val maxTemp: Double,
    @Json(name = "mintemp_c") val minTemp: Double,
    @Json(name = "avgtemp_c") val avgTemp: Double,
    @Json(name = "maxwind_kph") val maxWind: Double,
    @Json(name = "totalprecip_mm") val totalPrecip: Double,
    @Json(name = "avghumidity") val avgHumidity: Double,
    @Json(name = "daily_chance_of_rain") val chanceOfRain: Int,
    val condition: WeatherCondition
)

@JsonClass(generateAdapter = true)
data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    @Json(name = "moon_phase") val moonPhase: String
)

@JsonClass(generateAdapter = true)
data class Hour(
    @Json(name = "time_epoch") val timeEpoch: Long,
    val time: String,
    @Json(name = "temp_c") val temp: Double,
    @Json(name = "condition") val condition: WeatherCondition,
    @Json(name = "wind_kph") val windSpeed: Double,
    @Json(name = "wind_degree") val windDeg: Int,
    val humidity: Int,
    @Json(name = "feelslike_c") val feelsLike: Double
)