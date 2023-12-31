package com.example.pinnacleweather.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenWeatherMapAPI {

    @GET("data/2.5/weather") // Should move hardcoded strings into a const file
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("units") units: String, @Query("appid") apiKey: String): Response<OpenWeatherDataResponse>

    @GET("geo/1.0/direct") // Should move hardcoded strings into a const file
    suspend fun getGeoLocation(@Query("q") location: String, @Query("appid") apiKey: String): Response<List<OpenWeatherGeocodingResponse>>

}