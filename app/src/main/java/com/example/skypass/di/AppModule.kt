// di/AppModule.kt
package com.example.skypass.di

import android.content.Context
import com.example.skypass.data.local.AppDatabase
import com.example.skypass.data.local.dao.TravelCategoryDao
import com.example.skypass.data.local.dao.TravelEntryDao
import com.example.skypass.data.local.dao.TravelTagDao
import com.example.skypass.data.remote.WeatherApiService
import com.example.skypass.data.repository.TravelEntryRepository
import com.example.skypass.util.Constants
import com.example.skypass.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(okHttpClient: OkHttpClient): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.WEATHER_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    // Room Database related providers
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTravelEntryDao(database: AppDatabase): TravelEntryDao {
        return database.travelEntryDao()
    }

    @Provides
    @Singleton
    fun provideTravelTagDao(database: AppDatabase): TravelTagDao {
        return database.travelTagDao()
    }

    @Provides
    @Singleton
    fun provideTravelCategoryDao(database: AppDatabase): TravelCategoryDao {
        return database.travelCategoryDao()
    }

    @Provides
    @Singleton
    fun provideTravelEntryRepository(
        travelEntryDao: TravelEntryDao,
        travelTagDao: TravelTagDao,
        travelCategoryDao: TravelCategoryDao
    ): TravelEntryRepository {
        return TravelEntryRepository(travelEntryDao, travelTagDao, travelCategoryDao)
    }
}