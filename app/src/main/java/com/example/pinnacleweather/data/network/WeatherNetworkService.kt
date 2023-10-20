package com.example.pinnacleweather.data.network

import retrofit2.Response
import javax.inject.Inject

class WeatherNetworkService @Inject constructor(
    private val openWeatherMapAPI: OpenWeatherMapAPI) {

    private val API_KEY: String = "db62810e9c357e8c3f15f380a8635734"
    suspend fun getWeather(lat: Double, lon: Double): Response<OpenWeatherDataResponse> {
        return openWeatherMapAPI.getWeather(lat,lon,API_KEY)
    }


}