package com.example.pinnacleweather.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    Column {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Text(text = uiState.message, modifier = modifier)
        Button(onClick = viewModel::addRandom) {
            Text(text = "Add", modifier = modifier)
        }
    }
}