package com.example.demoapp.activities

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.utils.Utils.Companion.requestPermissionRationale
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

/**
 * An activity that displays a map showing the place at the device's current location.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val TAG = "MapsActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        createLocationRequest()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        map.uiSettings.isZoomControlsEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        map.setOnMarkerClickListener(this)
        setUpMap()
        setMapLongClick(map)
    }

    override fun onMarkerClick(p0: Marker?) = false

    /**
     * Method to place the map marker and defining the marker settings
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.ic_user_location)))
            .draggable(true)
        map.addMarker(markerOptions)
    }

    /**
     * Method to add marker on clicking map
     */
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            placeMarkerOnMap(latLng)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setUpMap()
            } else {
                Toast.makeText(baseContext, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                requestPermissionRationale(applicationContext, this,R.string.location_permission)
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Method that checks location permission enabled or not,
     * If permission is enabled method loads the current location
     * Else it will request location permissions
     */
    private fun setUpMap() {
        val permission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                location?.let {
                    lastLocation = it
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    placeMarkerOnMap(currentLatLng)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    /**
     * Method to request permission and track location updates
     */
    private fun startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Method to create a location request
     */
    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, sendEx.message.toString())
                }
            }
        }
    }

}