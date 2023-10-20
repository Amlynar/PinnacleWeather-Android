package com.example.pinnacleweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "weatherData")
data class LocalWeatherData (
    @PrimaryKey val id: String,
    var city: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    var temperature: Double,
    var weatherIcon: String,
    var weatherMain: String,
    var weatherDescription: String
    // TODO Add lastUpdated Timestamp
)