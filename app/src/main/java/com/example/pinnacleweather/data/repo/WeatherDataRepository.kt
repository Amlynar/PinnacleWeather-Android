package com.example.pinnacleweather.data.repo

import kotlinx.coroutines.flow.Flow

interface WeatherDataRepository {

    fun getWeatherDataStream(): Flow<WeatherData>

    suspend fun searchCity(cityName: String)

    suspend fun fetchWeatherByLatLonAndPersist(cityName: String, lat: Double, lon: Double)

    suspend fun fetchMostRecentWeatherDataIfExists()
}