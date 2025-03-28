package com.example.skypass.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Cross-reference entity for Many-to-Many relationship between TravelEntry and TravelTag
 */
@Entity(
    tableName = "travel_entry_tag_cross_ref",
    primaryKeys = ["entryId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TravelEntry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TravelTag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TravelEntryTagCrossRef(
    val entryId: Long,
    val tagId: Long
)