package com.example.pinnacleweather.di

import android.content.Context
import androidx.room.Room
import com.example.pinnacleweather.data.local.WeatherDataDao
import com.example.pinnacleweather.data.local.WeatherDataDatabase
import com.example.pinnacleweather.data.repo.WeatherDataRepository
import com.example.pinnacleweather.data.repo.WeatherDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @ViewModelScoped
    @Binds
    abstract fun bindWeatherDataRepository(repository: WeatherDataRepositoryImpl): WeatherDataRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providedDatabase(@ApplicationContext context: Context): WeatherDataDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDataDatabase::class.java,
            "WeatherData.db"
        ).build()
    }

    @Provides
    fun provideWeatherDataDao(database: WeatherDataDatabase): WeatherDataDao = database.weatherDataDao()
}