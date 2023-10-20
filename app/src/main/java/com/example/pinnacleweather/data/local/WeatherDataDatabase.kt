package com.example.pinnacleweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalWeatherData::class], version = 1, exportSchema = false)
abstract class WeatherDataDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao

}
