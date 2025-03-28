package com.example.skypass.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "travel_entries")
data class TravelEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val date: Date,
    val latitude: Double,
    val longitude: Double,
    val compassDirection: Float, // Direction in degrees
    val distance: Float? = null, // Optional distance traveled in km
    val category: String = "General", // Default category
    val createdAt: Date = Date(), // Timestamp when created
    val updatedAt: Date = Date() // Timestamp when updated
)

@Entity(tableName = "travel_categories")
data class TravelCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "#4A90E2"  // Default blue color
)