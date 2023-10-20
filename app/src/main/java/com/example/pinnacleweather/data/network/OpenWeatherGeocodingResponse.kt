package com.example.pinnacleweather.data.network

data class OpenWeatherGeocodingResponse (
    val lat: Double = 0.0,
    val lon: Double = 0.0
){
    override fun toString(): String {
        return "OpenWeatherGeocodingResponse(lat=$lat, lon=$lon)"
    }
}