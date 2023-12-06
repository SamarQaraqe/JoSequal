package com.josequal.maps.view_models

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.josequal.maps.R
import com.josequal.maps.models.Place

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val _placeItems = MutableLiveData<List<Place>>()
    val placeItems: LiveData<List<Place>>
        get() = _placeItems

    init {
        _placeItems.value = listOf(
            Place("Josequal", R.drawable.josequal, LatLng(31.9694574, 35.9136685), "Josequal description"),
            Place("Rotana", R.drawable.rotana, LatLng(31.9665891, 35.9029636), "Rotana description"),
            Place("The Ritz-Carlton", R.drawable.ritz, LatLng(31.955627, 35.8911347), "The Ritz-Carlton description")
        )
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    _currentLocation.postValue(it)
                }
            }
    }
}