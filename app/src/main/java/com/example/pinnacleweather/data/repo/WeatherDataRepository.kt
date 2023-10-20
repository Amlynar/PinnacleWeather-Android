package com.example.pinnacleweather.data.repo

import kotlinx.coroutines.flow.Flow

interface WeatherDataRepository {

    fun getWeatherDataStream(): Flow<WeatherData>

    suspend fun addRandom()
}