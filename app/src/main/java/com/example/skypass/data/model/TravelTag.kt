package com.example.skypass.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_tags")
data class TravelTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "#4285F4" // Default color (Google Blue)
)