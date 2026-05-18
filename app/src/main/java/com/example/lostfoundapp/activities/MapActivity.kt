package com.example.lostfoundapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lostfoundapp.R
import com.example.lostfoundapp.database.DatabaseHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private val fusedLocation by lazy { LocationServices.getFusedLocationProviderClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        dbHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val etRadius = findViewById<EditText>(R.id.etRadius)
        val btnSearch = findViewById<Button>(R.id.btnSearchNearby)

        btnSearch.setOnClickListener {
            val radiusStr = etRadius.text.toString()
            if (radiusStr.isNotEmpty()) {
                val radius = radiusStr.toDoubleOrNull()
                if (radius != null) {
                    showItemsWithinRadius(radius)
                } else {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            } else {
                // If empty, show all items
                showAllItems()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Enable "My Location" button if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }

        showAllItems()
    }

    private fun showAllItems() {
        googleMap.clear()
        val items = dbHelper.getAllItems()

        val builder = LatLngBounds.Builder()
        var hasItems = false

        for (item in items) {
            if (item.latitude != 0.0 || item.longitude != 0.0) {
                val position = LatLng(item.latitude, item.longitude)
                val markerColor = if (item.type == "Lost") {
                    BitmapDescriptorFactory.HUE_RED
                } else {
                    BitmapDescriptorFactory.HUE_GREEN
                }

                googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title("${item.type}: ${item.title}")
                        .snippet(item.location)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                )
                builder.include(position)
                hasItems = true
            }
        }

        if (hasItems) {
            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun showItemsWithinRadius(radiusKm: Double) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { currentLocation ->
            if (currentLocation == null) {
                Toast.makeText(this, "Current location unavailable. Make sure GPS is on.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            googleMap.clear()

            val userLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            val items = dbHelper.getAllItems()
            val builder = LatLngBounds.Builder()
            builder.include(userLatLng)
            var itemsInRange = 0

            for (item in items) {
                if (item.latitude == 0.0 && item.longitude == 0.0) continue

                val result = FloatArray(1)
                Location.distanceBetween(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    item.latitude,
                    item.longitude,
                    result
                )

                val distanceKm = result[0] / 1000.0

                if (distanceKm <= radiusKm) {
                    val pos = LatLng(item.latitude, item.longitude)
                    val markerColor = if (item.type == "Lost") {
                        BitmapDescriptorFactory.HUE_RED
                    } else {
                        BitmapDescriptorFactory.HUE_GREEN
                    }

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(item.latitude, item.longitude))
                            .title("${item.type}: ${item.title}")
                            .snippet("${item.location} - %.2f km away".format(distanceKm))
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                    )
                    builder.include(pos)
                    itemsInRange++
                }
            }

            if (itemsInRange > 0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150))
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
                Toast.makeText(this, "No items found within ${radiusKm}km", Toast.LENGTH_SHORT).show()
            }
        }
    }
}