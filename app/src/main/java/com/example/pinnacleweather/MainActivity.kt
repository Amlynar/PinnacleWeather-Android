package com.example.pinnacleweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pinnacleweather.data.location.LocationService
import com.example.pinnacleweather.ui.theme.PinnacleWeatherTheme
import com.example.pinnacleweather.weather.WeatherScreen
import com.example.pinnacleweather.weather.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var locationService: LocationService
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PinnacleWeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        viewModel.autoLoadWeatherData()
    }

    // Request the user for their location
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                locationService.permissionGranted()
                viewModel.autoLoadWeatherData()
            }
            else {
                locationService.permissionDenied()
                viewModel.autoLoadWeatherData()
            }
        }

    // Request the user for their location
    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationService.permissionGranted()
                viewModel.autoLoadWeatherData()

            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Here can display a message to the user notifying them the
                //  app can get their current locations weather with the right permissions
                locationService.permissionDenied()
                viewModel.autoLoadWeatherData()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

}