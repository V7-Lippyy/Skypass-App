package com.example.skypass.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val name: String,
    @Json(name = "local_names") val localNames: Map<String, String>?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)