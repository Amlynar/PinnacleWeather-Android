package com.example.pinnacleweather.data.repo

import android.util.Log
import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.local.WeatherDataDao
import com.example.pinnacleweather.data.network.OpenWeatherDataResponse
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
        localWeatherDataDao.getFirst()?.let {
            fetchWeatherByLatLonAndPersist(cityName = it.city, lon = it.lon, lat = it.lat)
        }
    }

    // We should consider using a Flow or observable here
    // to chain responses together and unify error handling
    override suspend fun searchCity(cityName: String) {
        weatherNetworkService.getGeoLocation(cityName).let {openWeatherGeocodingResponse ->
            if (!openWeatherGeocodingResponse.isSuccessful) {
                Log.e(TAG,openWeatherGeocodingResponse.errorBody().toString())
                throw Exception(openWeatherGeocodingResponse.errorBody().toString())
            }

            val geolocation = openWeatherGeocodingResponse.body()?.first()
            if (geolocation == null) {
                Log.e(TAG,"geolocation == null")
                throw Exception("geolocation == null")
            }

            fetchWeatherByLatLonAndPersist(cityName = cityName, lat = geolocation.lat, lon = geolocation.lon)
        }
    }

    override suspend fun fetchWeatherByLatLonAndPersist(cityName: String, lat: Double, lon: Double) {
        weatherNetworkService.getWeather(lat = lat, lon = lon).let { openWeatherDataResponse ->
            if(!openWeatherDataResponse.isSuccessful) {
                Log.e(TAG,openWeatherDataResponse.errorBody().toString())
                throw Exception(openWeatherDataResponse.errorBody().toString())
            }

            val weatherNetworkData = openWeatherDataResponse.body()
            if (weatherNetworkData == null) {
                Log.e(TAG, "weatherNetworkData == null")
                throw Exception("weatherNetworkData == null")
            }
            val lastUpdated = System.currentTimeMillis()
            localWeatherDataDao.deleteAll()
            val localWeatherData = toLocalWeatherData(cityName,lastUpdated,weatherNetworkData)
            localWeatherDataDao.upsert(localWeatherData)
        }
    }

    private fun toWeatherData(localWeatherData: LocalWeatherData): WeatherData = WeatherData(
        id = localWeatherData.id,
        city = localWeatherData.city,
        temperature = localWeatherData.temperature,
        weatherIcon = localWeatherData.weatherIcon,
        weatherMain = localWeatherData.weatherMain,
        weatherDescription = localWeatherData.weatherDescription,
        lastUpdated = localWeatherData.lastUpdated)

    private fun toLocalWeatherData(cityName: String, lastUpdated: Long, openWeatherDataResponse: OpenWeatherDataResponse): LocalWeatherData = LocalWeatherData(
        id = UUID.randomUUID().toString(),
        city = cityName,
        lat = openWeatherDataResponse.lat,
        lon = openWeatherDataResponse.lon,
        temperature = openWeatherDataResponse.main!!.temp,
        weatherIcon = weatherNetworkService.iconUrl(openWeatherDataResponse.weather!!.first().icon),
        weatherMain = openWeatherDataResponse.weather.first().main,
        weatherDescription = openWeatherDataResponse.weather.first().description,
        lastUpdated = lastUpdated
    )

}