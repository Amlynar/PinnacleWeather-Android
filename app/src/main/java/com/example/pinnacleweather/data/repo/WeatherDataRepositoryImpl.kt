package com.example.pinnacleweather.data.repo

import android.util.Log
import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.local.WeatherDataDao
import com.example.pinnacleweather.data.network.OpenWeatherDataResponse
import com.example.pinnacleweather.data.network.Weather
import com.example.pinnacleweather.data.network.WeatherNetworkService
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
    private val weatherNetworkService: WeatherNetworkService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : WeatherDataRepository {

    private val TAG: String = "WeatherDataRepository"

    override fun getWeatherDataStream(): Flow<WeatherData> {
        return localWeatherDataDao.observeOne()
            .filterNotNull()
            .map { localWeatherData ->
                withContext(dispatcher) {
                    toWeatherData(localWeatherData)
                }
            }
    }

    override suspend fun fetchMostRecentWeatherDataIfExists() {
        localWeatherDataDao.getFirst().let {
            // TODO fetchMostRecentWeatherDataIfExists
        }
    }

    // We should consider using a Flow or observable here
    // to chain responses together and unify error handling
    override suspend fun searchCity(cityName: String) {
        weatherNetworkService.getGeoLocation(cityName).let {openWeatherGeocodingResponse ->
            if (!openWeatherGeocodingResponse.isSuccessful) {
                Log.e(TAG,openWeatherGeocodingResponse.errorBody().toString())
                // TODO implement error handling
                return
            }

            val geolocation = openWeatherGeocodingResponse.body()?.first()
            if (geolocation == null) {
                Log.e(TAG,"geolocation == null")
                // TODO implement error handling
                return
            }

            weatherNetworkService.getWeather(lat = geolocation.lat, lon = geolocation.lon).let { openWeatherDataResponse ->
                if(!openWeatherDataResponse.isSuccessful) {
                    Log.e(TAG,openWeatherGeocodingResponse.errorBody().toString())
                    // TODO implement error handling
                    return
                }

                val weatherNetworkData = openWeatherDataResponse.body()
                if (weatherNetworkData == null) {
                    Log.e(TAG, "weatherNetworkData == null")
                    // TODO implement error handling
                    return
                }

                localWeatherDataDao.deleteAll()
                val localWeatherData = toLocalWeatherData(cityName,weatherNetworkData)
                localWeatherDataDao.upsert(localWeatherData)
            }
        }
    }

    private fun toWeatherData(localWeatherData: LocalWeatherData): WeatherData = WeatherData(
        id = localWeatherData.id,
        city = localWeatherData.city,
        temperature = localWeatherData.temperature,
        weatherIcon = localWeatherData.weatherIcon,
        weatherMain = localWeatherData.weatherMain,
        weatherDescription = localWeatherData.weatherDescription)

    private fun toLocalWeatherData(cityName: String, openWeatherDataResponse: OpenWeatherDataResponse): LocalWeatherData = LocalWeatherData(
        id = UUID.randomUUID().toString(),
        city = cityName,
        lat = openWeatherDataResponse.lat,
        lon = openWeatherDataResponse.lon,
        temperature = openWeatherDataResponse.main!!.temp,
        weatherIcon = openWeatherDataResponse.weather!!.first().icon,
        weatherMain = openWeatherDataResponse.weather.first().main,
        weatherDescription = openWeatherDataResponse.weather.first().description
    )

}