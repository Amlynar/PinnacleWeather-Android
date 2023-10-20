package com.example.pinnacleweather.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinnacleweather.data.repo.WeatherData
import com.example.pinnacleweather.data.repo.WeatherDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Error(val throwable: Throwable) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}

data class WeatherUiState (
    val message: String = "Weather Message",
    val errorMessage: String = "",
    val iconUrl: String = "")

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    private val errorMessageFlow = MutableStateFlow("")
    private val weatherDataAsyncFlow = weatherDataRepository.getWeatherDataStream()
        .map { Async.Success(it) }
        .catch<Async<WeatherData>> { throwable ->
            emit(Async.Error(throwable))
        }

    val uiState: StateFlow<WeatherUiState> = combine(
        errorMessageFlow, weatherDataAsyncFlow
    ) { errorMessage, weatherDataAsync ->
        produceWeatherUiState(errorMessage,weatherDataAsync)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = WeatherUiState()
        )

    fun autoLoadLastCity() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                weatherDataRepository.fetchMostRecentWeatherDataIfExists()
            }
            // Catching the base Exception is bad practise
            // in efforts to save time we can use this pattern
            //  this should be refactored later
            catch (e: Exception) {
                displayErrorMessage()
            }
        }
    }

    fun searchCity() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                val cityName = "New York"
                weatherDataRepository.searchCity(cityName)
            }
            // Catching the base Exception is bad practise
            // in efforts to save time we can use this pattern
            //  this should be refactored later
            catch (e: Exception) {
                displayErrorMessage()
            }
        }
    }

    private fun clearErrorMessage() {
        errorMessageFlow.value = ""
    }

    private fun displayErrorMessage() {
        errorMessageFlow.value = "There was an issue loading data"
    }

    private fun produceWeatherUiState(errorMessage: String, weatherDataLoad: Async<WeatherData>) =
        when (weatherDataLoad) {
            Async.Loading -> {
                WeatherUiState(message = "loading")
            }
            is Async.Error -> {
                Log.e("WeatherViewModel","Error", weatherDataLoad.throwable)
                WeatherUiState(errorMessage = errorMessage)
            }
            is Async.Success -> {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = Date(weatherDataLoad.data.lastUpdated)
                WeatherUiState(
                    message = "${weatherDataLoad.data.city} ${weatherDataLoad.data.weatherMain} ${weatherDataLoad.data.temperature} ${dateFormat.format(date)}",
                    errorMessage = errorMessage,
                    iconUrl = weatherDataLoad.data.weatherIcon)
            }
        }
}
