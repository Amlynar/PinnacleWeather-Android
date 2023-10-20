package com.example.pinnacleweather.data.network

data class OpenWeatherDataResponse (
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val main: Main?=null,
    val weather: List<Weather>?=null
)

data class Main (
    val temp: Double = 0.0
)

data class Weather(
    val id: Int = 0,
    val main: String = "",
    val description: String = "",
    val icon: String = ""
) {
    override fun toString(): String {
        return "Weather(id=$id, main='$main', description='$description', icon='$icon')"
    }
}