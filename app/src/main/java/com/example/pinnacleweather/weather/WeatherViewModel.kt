package com.example.pinnacleweather.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class WeatherUiState (val message: String = "Weather Message")

@HiltViewModel
class WeatherViewModel @Inject constructor() : ViewModel() {

    val uiState = WeatherUiState()

}