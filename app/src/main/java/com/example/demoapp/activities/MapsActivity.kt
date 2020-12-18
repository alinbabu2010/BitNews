package com.example.demoapp.activities

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.demoapp.R
import com.example.demoapp.utils.Utils.Companion.requestPermissionRationale
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException


/**
 * An activity that displays a map showing the place at the device's current location.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                lastLocation?.let {
                    placeMarkerOnMap(LatLng(it.latitude, it.longitude))
                }
            }
        }
        createLocationRequest()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        map.setOnMarkerClickListener(this)
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker?): View? {
                var view: View? = null
                try {
                    view = View.inflate(this@MapsActivity, R.layout.map_info_window, null)
                    val addressTxt: TextView = view.findViewById(R.id.addressTxt)
                    addressTxt.text = marker?.title
                } catch (e: Exception) {
                    Log.i(TAG, "getInfoContents: ${e.message}")
                }
                return view
            }

        })
        setUpMap()
        setMapLongClick(map)
    }

    override fun onMarkerClick(p0: Marker?) = false

    /**
     * Method to get place name from location coordinates
     * Reverse geocode method used
     */
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        var addressText = ""
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.let {
                addressText = it[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }
        return addressText
    }

    /**
     * Method to get location coordinates on giving a place name
     * Geocode method used
     */
    private fun searchLocation(query: String?) {
        var addressList: List<Address>? = null
        if (query?.isNotEmpty() == true) {
            val geocoder = Geocoder(this)
            try {
                addressList = geocoder.getFromLocationName(query, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addressList?.isNullOrEmpty() == true){
                Toast.makeText(this, "Couldn't find the place", Toast.LENGTH_SHORT).show()
            } 
            else {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }

        }
    }

    /**
     * Method to place the map marker and defining the marker settings
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val address = getAddress(location)
        map.addMarker(
            MarkerOptions()
                .position(location)
                .title(address)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.ic_user_location
                        )
                    )
                )
                .draggable(true)
        )
        val lastKnownLocation = lastLocation?.let {
            LatLng(it.latitude,it.longitude)
        }
        var polygon = addPolyLine(lastKnownLocation,location)
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(marker: Marker?) {
                polygon?.remove()
                marker?.hideInfoWindow()
            }
            override fun onMarkerDrag(marker: Marker?) {
                polygon?.remove()
                marker?.hideInfoWindow()
            }
            override fun onMarkerDragEnd(marker: Marker?) {
                marker?.let {
                    polygon?.remove()
                    it.title = getAddress(it.position)
                    it.showInfoWindow()
                    polygon = addPolyLine(lastKnownLocation,it.position)
                }
            }
        })
        map.setOnPolygonClickListener {
            it.remove()
        }
    }

    /**
     * Method to add a polyline on map
     */
    private fun addPolyLine(lastKnownLocation: LatLng?, location: LatLng): Polygon? {
        val polygon = map.addPolygon(PolygonOptions()
            .clickable(true)
            .add(lastKnownLocation,location))
        polygon.strokeColor = Color.RED
        return polygon
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
                requestPermissionRationale(applicationContext, this, R.string.location_permission)
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
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                location?.let {
                    lastLocation = it
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    placeMarkerOnMap(currentLatLng)
                    map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                }
            }
            map.isMyLocationEnabled = true
        }
    }

    /**
     * Overriding [onPause] to remove location updating on activity pause state
     */
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Overriding [onResume] to check location update state
     */
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
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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
        // On Failure checks GPS on or off if off ask user to turn on GPS
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

    /**
     * Overriding [onCreateOptionsMenu] to perform search view operations
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchViewItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchViewItem.actionView as SearchView
        searchView.isFocusable = false
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchLocation(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

}