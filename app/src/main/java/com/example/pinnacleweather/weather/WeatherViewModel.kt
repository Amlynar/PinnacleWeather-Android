package com.example.pinnacleweather.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinnacleweather.data.repo.WeatherDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState (val message: String = "Weather Message")

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    val uiState = WeatherUiState()

    fun addRandom() {
        viewModelScope.launch {
            weatherDataRepository.addRandom()
        }

    }

}
