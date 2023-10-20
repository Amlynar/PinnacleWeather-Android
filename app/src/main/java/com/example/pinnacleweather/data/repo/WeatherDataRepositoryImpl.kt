package com.example.pinnacleweather.data.repo

import android.util.Log
import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.local.WeatherDataDao
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDataRepositoryImpl @Inject constructor(
    private val localWeatherDataDao: WeatherDataDao
) : WeatherDataRepository {
    override suspend fun addRandom() {
        localWeatherDataDao.deleteAll()
        localWeatherDataDao.upsert(
            LocalWeatherData(
                UUID.randomUUID().toString(),
                "city")
        )
        for (localWeatherData in localWeatherDataDao.getAll()) {
            Log.d("test",localWeatherData.toString())
        }
    }
}