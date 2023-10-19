package com.example.pinnacleweather.weather

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    Text(text = viewModel.uiState.message, modifier = modifier)
}