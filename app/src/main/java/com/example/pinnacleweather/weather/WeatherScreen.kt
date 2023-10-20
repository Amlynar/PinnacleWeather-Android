package com.example.pinnacleweather.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    Column {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Text(text = uiState.message, modifier = modifier)
        Button(onClick = viewModel::searchCity) {
            Text(text = "Add", modifier = modifier)
        }
        GlideImage(model = "https://openweathermap.org/img/wn/10d@4x.png", contentDescription = "", modifier = modifier) // Experimental library
    }
}