package com.example.pinnacleweather.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDataDao {

    @Query("SELECT * FROM weatherData")
    fun observeAll(): Flow<List<LocalWeatherData>>

    @Upsert
    suspend fun upsert(weatherData: LocalWeatherData)

    @Query("DELETE FROM weatherData")
    suspend fun deleteAll()

    @Query("SELECT * FROM weatherData")
    suspend fun getAll(): List<LocalWeatherData>
}
