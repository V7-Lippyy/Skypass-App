package com.example.skypass.data.repository

import com.example.skypass.data.local.dao.TravelCategoryDao
import com.example.skypass.data.local.dao.TravelEntryDao
import com.example.skypass.data.local.dao.TravelTagDao
import com.example.skypass.data.model.TravelCategory
import com.example.skypass.data.model.TravelEntry
import com.example.skypass.data.model.TravelEntryTagCrossRef
import com.example.skypass.data.model.TravelEntryWithTags
import com.example.skypass.data.model.TravelTag
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelEntryRepository @Inject constructor(
    private val travelEntryDao: TravelEntryDao,
    private val travelTagDao: TravelTagDao,
    private val travelCategoryDao: TravelCategoryDao
) {
    // Travel Entry Operations
    fun getAllTravelEntries(): Flow<List<TravelEntry>> {
        return travelEntryDao.getAllTravelEntries()
    }

    fun getAllTravelEntriesWithTags(): Flow<List<TravelEntryWithTags>> {
        return travelEntryDao.getAllTravelEntriesWithTags()
    }

    fun getTravelEntryWithTags(entryId: Long): Flow<TravelEntryWithTags> {
        return travelEntryDao.getTravelEntryWithTags(entryId)
    }

    suspend fun getTravelEntryById(entryId: Long): TravelEntry? {
        return travelEntryDao.getTravelEntryById(entryId)
    }

    fun getTravelEntriesByCategory(category: String): Flow<List<TravelEntry>> {
        return travelEntryDao.getTravelEntriesByCategory(category)
    }

    fun getTravelEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<TravelEntry>> {
        return travelEntryDao.getTravelEntriesByDateRange(startDate, endDate)
    }

    fun searchTravelEntries(searchQuery: String): Flow<List<TravelEntry>> {
        return travelEntryDao.searchTravelEntries(searchQuery)
    }

    fun getAllCategories(): Flow<List<String>> {
        return travelEntryDao.getAllCategories()
    }

    // Save or update entry with tags
    suspend fun saveTravelEntryWithTags(entry: TravelEntry, tags: List<Long>) {
        val entryId = travelEntryDao.insert(entry)

        // Delete old tag relationships if updating
        if (entry.id != 0L) {
            travelTagDao.deleteAllTagsForEntry(entry.id)
        }

        // Insert new tag relationships
        tags.forEach { tagId ->
            travelTagDao.insertEntryTagCrossRef(
                TravelEntryTagCrossRef(entryId = entryId, tagId = tagId)
            )
        }
    }

    suspend fun updateTravelEntry(entry: TravelEntry) {
        travelEntryDao.update(entry)
    }

    suspend fun deleteTravelEntry(entry: TravelEntry) {
        travelEntryDao.delete(entry)
    }

    // Tag Operations
    fun getAllTags(): Flow<List<TravelTag>> {
        return travelTagDao.getAllTags()
    }

    suspend fun getTagById(tagId: Long): TravelTag? {
        return travelTagDao.getTagById(tagId)
    }

    suspend fun insertTag(tag: TravelTag): Long {
        return travelTagDao.insert(tag)
    }

    suspend fun updateTag(tag: TravelTag) {
        travelTagDao.update(tag)
    }

    suspend fun deleteTag(tag: TravelTag) {
        travelTagDao.delete(tag)
    }

    fun getTagsForEntry(entryId: Long): Flow<List<TravelTag>> {
        return travelTagDao.getTagsForEntry(entryId)
    }

    // Category Operations
    fun getAllTravelCategories(): Flow<List<TravelCategory>> {
        return travelCategoryDao.getAllCategories()
    }

    suspend fun getCategoryByName(name: String): TravelCategory? {
        return travelCategoryDao.getCategoryByName(name)
    }

    suspend fun createCategory(name: String, color: String = "#4A90E2"): Long {
        // Check if category already exists
        val existingCategory = travelCategoryDao.getCategoryByName(name)
        if (existingCategory != null) {
            return existingCategory.id
        }

        // Create new category
        val newCategory = TravelCategory(name = name, color = color)
        return travelCategoryDao.insert(newCategory)
    }

    suspend fun updateCategory(category: TravelCategory) {
        travelCategoryDao.update(category)
    }

    suspend fun deleteCategory(category: TravelCategory) {
        travelCategoryDao.delete(category)
    }

    suspend fun categoryExists(name: String): Boolean {
        return travelCategoryDao.categoryExists(name)
    }
}