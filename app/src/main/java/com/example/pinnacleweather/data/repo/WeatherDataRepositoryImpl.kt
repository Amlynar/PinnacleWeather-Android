package com.example.pinnacleweather.data.repo

import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.local.WeatherDataDao
import com.example.pinnacleweather.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDataRepositoryImpl @Inject constructor(
    private val localWeatherDataDao: WeatherDataDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : WeatherDataRepository {


    override fun getWeatherDataStream(): Flow<WeatherData> {
        return localWeatherDataDao.observeOne()
            .filterNotNull()
            .map { localWeatherData ->
                withContext(dispatcher) {
                    toWeatherData(localWeatherData)
                }
            }
    }

    override suspend fun addRandom() {
        localWeatherDataDao.deleteAll()
        localWeatherDataDao.upsert(
            LocalWeatherData(
                UUID.randomUUID().toString(),
                "city")
        )
//        for (localWeatherData in localWeatherDataDao.getAll()) {
//            Log.d("test",localWeatherData.toString())
//        }
    }

    private fun toWeatherData(localWeatherData: LocalWeatherData): WeatherData = WeatherData(
        id = localWeatherData.id,
        city = localWeatherData.city)

}