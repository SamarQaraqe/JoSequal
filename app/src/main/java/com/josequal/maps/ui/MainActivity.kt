package com.josequal.maps.ui

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.josequal.maps.R
import com.josequal.maps.adapters.PlacesAdapter
import com.josequal.maps.databinding.ActivityMainBinding
import com.josequal.maps.models.Place
import com.josequal.maps.view_models.MapViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: MapViewModel
    private lateinit var placesAdapter: PlacesAdapter
    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        binding.mapView.getMapAsync(this)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        viewModel.currentLocation.observe(this) { location ->
            location?.let {
                updateMapLocation(LatLng(it.latitude, it.longitude))
            }
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun moveCameraToLocation(location: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    private fun updateMapLocation(latLng: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        viewModel.getCurrentLocation()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it

            binding.rvPlaces.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            placesAdapter = PlacesAdapter { place ->
                showImageOnMap(place)
            }
            binding.rvPlaces.adapter = placesAdapter

            viewModel.placeItems.observe(this) { placeItems ->
                placesAdapter.setPlaceItems(placeItems)

                googleMap.clear()
                placeItems.forEach { placeItem ->
                    val markerOptions = MarkerOptions()
                        .position(placeItem.location)
                        .title(placeItem.title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

                    val marker = googleMap.addMarker(markerOptions)
                    marker?.tag = placeItem

                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(p0: Marker): View? {
                            return null
                        }

                        override fun getInfoWindow(p0: Marker): View? {
                            val infoView = layoutInflater.inflate(R.layout.custom_info_window, null)
                            val imageView: ImageView = infoView.findViewById(R.id.info_window_image)
                            val titleTextView: TextView = infoView.findViewById(R.id.info_window_title)

                            val placeItem = p0.tag as? Place
                            placeItem?.let {
                                Glide.with(this@MainActivity).load(it.image).into(imageView)
                                titleTextView.text = it.title
                            }

                            return infoView
                        }

                    })
                }

                googleMap.setOnMarkerClickListener { marker ->
                    val placeItem = marker.tag as? Place
                    placeItem?.let {
                        showItemDetailDialog(it)
                    }

                    false
                }
            }
        }
    }

    private fun showItemDetailDialog(placeItem: Place) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_item_details)

        val image: ImageView = dialog.findViewById(R.id.image)
        val title: TextView = dialog.findViewById(R.id.title)
        val description: TextView = dialog.findViewById(R.id.description)

        Glide.with(this).load(placeItem.image).into(image)
        title.text = placeItem.title
        description.text = placeItem.description
        dialog.show()
    }

    private fun showImageOnMap(placeItem: Place) {
        moveCameraToLocation(placeItem.location)
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(placeItem.location)
                .title(placeItem.title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
        )
        marker?.tag = placeItem
        marker?.showInfoWindow()
    }
}