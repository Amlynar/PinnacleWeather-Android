package com.example.pinnacleweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "weatherData")
data class LocalWeatherData (
    @PrimaryKey val id: String,
    var city: String,
)