package com.example.pinnacleweather.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenWeatherMapAPI {

    @GET("data/2.5/weather")
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiKey: String): Response<OpenWeatherDataResponse>

//    @GET("get/1.0/direct?q={cityName},{countryCode}&appid={apiKey}")
//    suspend fun getLatLon(@Path(value = "cityName") cityName: String, @Path(value = "countryCode") countryCode: String,@Path(value = "cityName") apiKey: String): Response<List<OpenWeatherGeocodingResponse>>


    @GET("geo/1.0/direct")
    suspend fun getGeoLocation(@Query("q") location: String, @Query("appid") apiKey: String): Response<List<OpenWeatherGeocodingResponse>>

    @GET("get/1.0/direct")
    suspend fun getLatLon(@Query("cityName") cityName: String, @Query("countryCode") countryCode: String,@Query("appid") apiKey: String): Response<List<OpenWeatherGeocodingResponse>>
}