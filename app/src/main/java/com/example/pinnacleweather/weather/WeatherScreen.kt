package com.example.pinnacleweather.weather

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    Column {
        Text(text = viewModel.uiState.message, modifier = modifier)
        Button(onClick = viewModel::addRandom) {
            Text(text = "Add", modifier = modifier)
        }
    }
}