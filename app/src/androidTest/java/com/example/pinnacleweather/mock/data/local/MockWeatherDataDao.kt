package com.example.pinnacleweather.mock.data.local

import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.local.WeatherDataDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MockWeatherDataDao: WeatherDataDao {

    private var weatherDataList: MutableList<LocalWeatherData> = mutableListOf()

    override fun observeAll(): Flow<List<LocalWeatherData>> {
        TODO("Not yet implemented")
    }

    override fun observeOne(): Flow<LocalWeatherData> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(weatherData: LocalWeatherData) {
        weatherDataList.add(weatherData)
    }

    override suspend fun deleteAll() {
        weatherDataList.clear()
    }

    override suspend fun getAll(): List<LocalWeatherData> {
        return weatherDataList
    }

    override suspend fun getFirst(): LocalWeatherData {
        return weatherDataList.first()
    }
}