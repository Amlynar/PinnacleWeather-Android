package com.example.pinnacleweather.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinnacleweather.data.repo.WeatherData
import com.example.pinnacleweather.data.repo.WeatherDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Error(val throwable: Throwable) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}

data class WeatherUiState (val message: String = "Weather Message")

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    val uiState: StateFlow<WeatherUiState> =
        weatherDataRepository.getWeatherDataStream()
            .map { Async.Success(it) }
            .catch<Async<WeatherData>> { throwable ->
                emit(Async.Error(throwable))
            }
            .map { taskAsync -> produceWeatherUiState(taskAsync) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = WeatherUiState()
            )

    fun autoLoadLastCity() {
        viewModelScope.launch {
            weatherDataRepository.fetchMostRecentWeatherDataIfExists()
        }
    }

    fun searchCity() {
        viewModelScope.launch {
            val cityName = "New York"
            weatherDataRepository.searchCity(cityName)
        }

    }

    fun addRandom() {
        viewModelScope.launch {
            weatherDataRepository.addRandom()
        }

    }


    private fun produceWeatherUiState(weatherDataLoad: Async<WeatherData>) =
        when (weatherDataLoad) {
            Async.Loading -> {
                WeatherUiState(message = "loading")
            }
            is Async.Error -> {
                Log.e("WeatherViewModel","Error", weatherDataLoad.throwable)
                WeatherUiState(message = "error")
            }
            is Async.Success -> {
                WeatherUiState(message = "${weatherDataLoad.data.city} ${weatherDataLoad.data.weatherMain} ${weatherDataLoad.data.temperature}")
            }
        }
}
