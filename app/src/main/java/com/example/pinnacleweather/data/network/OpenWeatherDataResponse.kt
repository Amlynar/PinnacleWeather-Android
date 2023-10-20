package com.example.pinnacleweather.data.network

data class OpenWeatherDataResponse (
    val weather: List<Weather>?=null
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