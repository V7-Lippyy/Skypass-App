package com.example.skypass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.skypass.data.model.TravelEntry
import com.example.skypass.data.model.TravelEntryWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

@Dao
interface TravelEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(travelEntry: TravelEntry): Long

    @Update
    suspend fun update(travelEntry: TravelEntry)

    @Delete
    suspend fun delete(travelEntry: TravelEntry)

    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    fun getAllTravelEntries(): Flow<List<TravelEntry>>

    @Query("SELECT * FROM travel_entries WHERE id = :entryId")
    suspend fun getTravelEntryById(entryId: Long): TravelEntry?

    @Transaction
    @Query("SELECT * FROM travel_entries WHERE id = :entryId")
    fun getTravelEntryWithTags(entryId: Long): Flow<TravelEntryWithTags>

    @Transaction
    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    fun getAllTravelEntriesWithTags(): Flow<List<TravelEntryWithTags>>

    @Query("SELECT * FROM travel_entries WHERE category = :category ORDER BY date DESC")
    fun getTravelEntriesByCategory(category: String): Flow<List<TravelEntry>>

    @Query("SELECT * FROM travel_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTravelEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<TravelEntry>>

    @Query("SELECT * FROM travel_entries WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    fun searchTravelEntries(searchQuery: String): Flow<List<TravelEntry>>

    @Query("SELECT DISTINCT category FROM travel_entries ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    // New functions for category management

    @Query("INSERT INTO travel_entries (title, description, date, latitude, longitude, compassDirection, distance, category, createdAt, updatedAt) " +
            "VALUES ('', '', :timestamp, 0.0, 0.0, 0.0, NULL, :categoryName, :timestamp, :timestamp)")
    suspend fun createDummyEntryWithCategory(categoryName: String, timestamp: Long = System.currentTimeMillis()): Long

    @Query("DELETE FROM travel_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Long)

    @Query("UPDATE travel_entries SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateCategoryName(oldCategory: String, newCategory: String)

    @Query("SELECT COUNT(*) FROM travel_entries WHERE category = :category")
    suspend fun getEntriesCountByCategory(category: String): Int

    // The following is a utility function to help insert predefined categories if needed
    @Transaction
    suspend fun insertDefaultCategories(categories: List<String>) {
        val timestamp = System.currentTimeMillis()
        categories.forEach { category ->
            // Check if category already exists
            val existingCategories = getAllCategories().first()
            if (!existingCategories.contains(category)) {
                val dummyId = createDummyEntryWithCategory(category, timestamp)
                // Delete dummy entry to avoid cluttering the database
                // The category will remain in entries using getAllCategories()
                deleteEntryById(dummyId)
            }
        }
    }
}