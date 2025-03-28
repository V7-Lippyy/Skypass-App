package com.example.skypass.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class that represents a TravelEntry with its associated Tags
 */
data class TravelEntryWithTags(
    @Embedded val travelEntry: TravelEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TravelEntryTagCrossRef::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TravelTag>
)