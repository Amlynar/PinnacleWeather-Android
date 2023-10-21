package com.example.pinnacleweather.mock.data.network

import com.example.pinnacleweather.data.network.Main
import com.example.pinnacleweather.data.network.OpenWeatherDataResponse
import com.example.pinnacleweather.data.network.OpenWeatherGeocodingResponse
import com.example.pinnacleweather.data.network.OpenWeatherMapAPI
import com.example.pinnacleweather.data.network.Weather
import retrofit2.Response

class MockOpenWeatherMapAPI: OpenWeatherMapAPI {

    var openWeatherDataResponse: Response<OpenWeatherDataResponse> = Response.success(
        OpenWeatherDataResponse(
            lat = 70.4,
            lon = 45.7,
            main = Main(
                temp = 61.1
            ),
            mutableListOf(
                Weather(
                    id = 7,
                    main = "Rain",
                    description = "Mostly Cloudy",
                    icon = ""
                )
            )
        ))

    var openWeatherGeocodingResponse: Response<List<OpenWeatherGeocodingResponse>> = Response.success(
        mutableListOf(
                OpenWeatherGeocodingResponse(
                    lat = 70.4,
                    lon = 45.7
                )
            )
        )

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        apiKey: String
    ): Response<OpenWeatherDataResponse> {
        return openWeatherDataResponse
    }

    override suspend fun getGeoLocation(
        location: String,
        apiKey: String
    ): Response<List<OpenWeatherGeocodingResponse>> {
        return openWeatherGeocodingResponse
    }
}