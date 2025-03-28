package com.example.skypass.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.skypass.data.local.dao.TravelCategoryDao
import com.example.skypass.data.local.dao.TravelEntryDao
import com.example.skypass.data.local.dao.TravelTagDao
import com.example.skypass.data.model.TravelCategory
import com.example.skypass.data.model.TravelEntry
import com.example.skypass.data.model.TravelEntryTagCrossRef
import com.example.skypass.data.model.TravelTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TravelEntry::class,
        TravelTag::class,
        TravelEntryTagCrossRef::class,
        TravelCategory::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun travelEntryDao(): TravelEntryDao
    abstract fun travelTagDao(): TravelTagDao
    abstract fun travelCategoryDao(): TravelCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "skypass_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Database callback to populate initial data
        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // Populate default categories
                        populateDefaultCategories(database.travelCategoryDao())
                    }
                }
            }

            private suspend fun populateDefaultCategories(categoryDao: TravelCategoryDao) {
                // Add default categories
                val defaultCategories = listOf(
                    TravelCategory(name = "General"),
                    TravelCategory(name = "Hiking", color = "#4CAF50"),  // Green
                    TravelCategory(name = "City Tour", color = "#9C27B0"), // Purple
                    TravelCategory(name = "Road Trip", color = "#FF9800"), // Orange
                    TravelCategory(name = "Vacation", color = "#E91E63")  // Pink
                )

                categoryDao.insertDefaultCategories(defaultCategories)
            }
        }
    }
}