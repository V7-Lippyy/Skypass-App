package com.example.skypass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skypass.data.model.TravelCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: TravelCategory): Long

    @Update
    suspend fun update(category: TravelCategory)

    @Delete
    suspend fun delete(category: TravelCategory)

    @Query("SELECT * FROM travel_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<TravelCategory>>

    @Query("SELECT * FROM travel_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): TravelCategory?

    @Query("SELECT * FROM travel_categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): TravelCategory?

    @Query("SELECT EXISTS(SELECT 1 FROM travel_categories WHERE name = :name LIMIT 1)")
    suspend fun categoryExists(name: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultCategories(categories: List<TravelCategory>)
}