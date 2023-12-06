package com.josequal.maps.models

import com.google.android.gms.maps.model.LatLng

data class Place(val title: String, val image: Int, val location: LatLng, val description: String)

