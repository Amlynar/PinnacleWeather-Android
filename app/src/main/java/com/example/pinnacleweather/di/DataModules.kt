package com.example.pinnacleweather.di

import android.content.Context
import androidx.room.Room
import com.example.pinnacleweather.data.local.WeatherDataDao
import com.example.pinnacleweather.data.local.WeatherDataDatabase
import com.example.pinnacleweather.data.network.OpenWeatherMapAPI
import com.example.pinnacleweather.data.network.WeatherNetworkService
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val DEBUG: Boolean = true
    private val BASE_URL: String = "https://api.openweathermap.org/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        // Can create a debug/release build to add/remove the logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideOpenWeatherMapAPI(retrofit: Retrofit) = retrofit.create(OpenWeatherMapAPI::class.java)

    @Singleton
    @Provides
    fun provideWeatherNetworkService(openWeatherMapAPI: OpenWeatherMapAPI): WeatherNetworkService = WeatherNetworkService(openWeatherMapAPI = openWeatherMapAPI)

}
