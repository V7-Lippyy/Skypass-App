package com.example.skypass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skypass.data.model.TravelEntryTagCrossRef
import com.example.skypass.data.model.TravelTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(travelTag: TravelTag): Long

    @Update
    suspend fun update(travelTag: TravelTag)

    @Delete
    suspend fun delete(travelTag: TravelTag)

    @Query("SELECT * FROM travel_tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TravelTag>>

    @Query("SELECT * FROM travel_tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TravelTag?

    // Cross-reference operations for entry-tag relationship
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntryTagCrossRef(crossRef: TravelEntryTagCrossRef)

    @Delete
    suspend fun deleteEntryTagCrossRef(crossRef: TravelEntryTagCrossRef)

    @Query("DELETE FROM travel_entry_tag_cross_ref WHERE entryId = :entryId")
    suspend fun deleteAllTagsForEntry(entryId: Long)

    @Query("SELECT * FROM travel_tags " +
            "INNER JOIN travel_entry_tag_cross_ref ON travel_tags.id = travel_entry_tag_cross_ref.tagId " +
            "WHERE travel_entry_tag_cross_ref.entryId = :entryId")
    fun getTagsForEntry(entryId: Long): Flow<List<TravelTag>>
}