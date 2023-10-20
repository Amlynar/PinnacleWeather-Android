package com.example.pinnacleweather.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    // This compose UI is not very pleasant and can be updated int he future with
    //  a better design
    // Also a navigation with multiple screens would be nice to save locations
    Column {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        OutlinedTextField(value = viewModel.searchEntry, onValueChange = viewModel::updateUsername)
        Text(text = uiState.message, modifier = modifier)
        Button(onClick = viewModel::searchCity) {
            Text(text = "Search", modifier = modifier)
        }
        GlideImage(model = uiState.iconUrl, contentDescription = "", modifier = modifier) // Glide handles image downloading and caching
        Text(text = uiState.errorMessage, modifier = modifier)
    }
}
