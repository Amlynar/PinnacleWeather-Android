package com.example.pinnacleweather.data.location

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class LocationPermissionStatus {
    GRANTED, DENIED, UNKNOWN
}

// A wrapper class around the Google LocationServices class
class LocationService @Inject constructor(
    @ApplicationContext private val applicationContext: Context
){

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

    private var locationPermissionStatus: LocationPermissionStatus = LocationPermissionStatus.UNKNOWN

    fun permissionGranted() {
        locationPermissionStatus = LocationPermissionStatus.GRANTED
    }

    fun permissionDenied() {
        locationPermissionStatus = LocationPermissionStatus.DENIED
    }

    fun permissionStatus() = locationPermissionStatus

    // Location permissions are requested an handled before this function is ever called
    fun lastKnownLocation() = fusedLocationClient.lastLocation

}