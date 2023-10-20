package com.example.pinnacleweather.data.repo

data class WeatherData(
    val id: String,
    var city: String,
    var temperature: Double,
    var weatherIcon: String,
    var weatherMain: String,
    var weatherDescription: String,
    var lastUpdated: Long
)
