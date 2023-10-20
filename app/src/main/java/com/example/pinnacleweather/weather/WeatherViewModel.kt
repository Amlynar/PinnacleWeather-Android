package com.example.pinnacleweather.weather

import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinnacleweather.data.location.LocationPermissionStatus
import com.example.pinnacleweather.data.location.LocationService
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
    object Loading : Async<Nothing>() // Not used or implemented at the moment but could be used to display a spinner or loading icon

    data class Error(val throwable: Throwable) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}

data class WeatherUiState (
    val message: String = "Weather Message", // Strings displayed to the user should be int he strings.xml and properly translated per language
    val errorMessage: String = "",
    val iconUrl: String = "")

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
    private val locationService: LocationService
) : ViewModel() {

    // Here we create two Flow objects to notify the UI when there are data changes the user
    //  should be aware of
    //  errorMessageFlow with display text to the user when an issue occurs this can be handled better
    //  weatherDataAsyncFlow will emit updates to the UI when something the in Room local database changes
    private val errorMessageFlow = MutableStateFlow("")
    private val weatherDataAsyncFlow = weatherDataRepository.getWeatherDataStream()
        .map { Async.Success(it) }
        .catch<Async<WeatherData>> { throwable ->
            emit(Async.Error(throwable))
        }

    // Combine both Flow objects to have one data flow for UI updates
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


    var searchEntry by mutableStateOf("") // Stores the current text entered in the OutlinedTextField

    fun updateUsername(input: String) {
        searchEntry = input
    }

    // To be called on app being called to the foreground
    //  here we will check permissions to decide if we should
    //  update the weather data using the users last known location
    //  or fall back and check if there is any recent search in the local database
    fun autoLoadWeatherData() {
        when (locationService.permissionStatus()) {
            LocationPermissionStatus.GRANTED -> {
                locationService.lastKnownLocation().addOnSuccessListener {location ->
                    if (location != null) { // According to Docs location and be null in rare occasions
                        fetchWeatherByLatLonAndPersist(lat = location.latitude, lon = location.longitude)
                    }
                    else {
                        autoLoadLastCity()
                    }
                }
            }
            LocationPermissionStatus.DENIED -> {
                autoLoadLastCity()
            }
            LocationPermissionStatus.UNKNOWN -> {} // If this function is called without the permissions being updated in time
        }
    }

    // To be called when the user clicks the Search button
    //  we will do a quick null check and attempt to make
    //  an API call for the current weather data at the entered city
    fun searchCity() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                if (searchEntry.isNotEmpty()) { // will want more validation
                    // in a future feature showing a list or past searches or
                    // showing recommendations based on input should be considered
                    val cityName = searchEntry
                    searchEntry = ""
                    weatherDataRepository.searchCity(cityName)
                }
            }
            // Catching the base Exception is bad practise
            // in efforts to save time we can use this pattern
            //  this should be refactored later
            catch (e: Exception) {
                displayErrorMessage()
            }
        }
    }

    // This helper function will check the local database for
    //  saved weather data and display it if there is a row
    private fun autoLoadLastCity() {
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

    // This helper function will attempt to make an API request
    //  using latitude and longitude values to get the current weather
    //  if successful the data will be persisted locally and displayed
    //  to the user
    private fun fetchWeatherByLatLonAndPersist(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                weatherDataRepository.fetchWeatherByLatLonAndPersist(
                    cityName = "", // This API call does not seem to return the nearest city name, maybe this can be an added feature in the future
                    lat = lat,
                    lon = lon)
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

    // This will display an error message tot he user if we encounter
    //  an exception at any point
    private fun displayErrorMessage() {
        errorMessageFlow.value = "There was an issue loading data" // Strings displayed to the user should be in the strings.xml file and be translated per language
    }

    private fun produceWeatherUiState(errorMessage: String, weatherDataLoad: Async<WeatherData>) =
        when (weatherDataLoad) {
            Async.Loading -> { // Not currently implemented
                WeatherUiState(message = "loading") // Strings displayed to the user should be in the strings.xml file and be translated per language
            }
            is Async.Error -> {
                Log.e("WeatherViewModel","Error", weatherDataLoad.throwable)
                WeatherUiState(errorMessage = errorMessage)
            }
            is Async.Success -> {
                // This can be better displayed to the user once a better UI is designed
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = Date(weatherDataLoad.data.lastUpdated)
                WeatherUiState(
                    message = "${weatherDataLoad.data.city} ${weatherDataLoad.data.weatherMain} ${weatherDataLoad.data.temperature} ${dateFormat.format(date)}",
                    errorMessage = errorMessage,
                    iconUrl = weatherDataLoad.data.weatherIcon)
            }
        }
}
