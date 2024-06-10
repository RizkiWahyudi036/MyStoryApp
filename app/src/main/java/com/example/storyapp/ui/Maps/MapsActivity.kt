package com.example.storyapp.ui.Maps

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.storyapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.preferences.SharedPreferences
import com.example.storyapp.utils.MyResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var pref: SharedPreferences

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null

    private val mapsViewModel by viewModels<MapsViewModel> {
        MapsViewModel.Factory(this)
    }

    private var storyName: MutableList<String>? = null
    private var storyDesc: MutableList<String>? = null
    private var storyLat: MutableList<Float>? = null
    private var storyLong: MutableList<Float>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferences(this)
        pref.setStatusLogin(true)

        binding.progressBar.visibility = View.GONE

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        fusedLocationProvider?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(
                    this
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        listMapsUser()
    }

    private fun listMapsUser(){
        val token = "Bearer ${pref.getUserToken()}"
        mapsViewModel.getStoriesWithLocation(token).observe(this@MapsActivity){
            if(it != null){
                when(it){
                    is MyResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val maps = it.list.listStory
                        maps.forEach { list ->
                            val latLng = LatLng(list.lat.toString().toDouble(), list.lon.toString().toDouble())
                            mMap.addMarker(MarkerOptions().position(latLng).title(list.name))
                        }
                        storyName = ArrayList()
                        storyDesc = ArrayList()
                        storyLat = ArrayList()
                        storyLong = ArrayList()
                        for (i in maps.indices) {
                            storyName?.add(maps[i].name.toString())
                            storyDesc?.add(maps[i].description.toString())
                            storyLat?.add(maps[i].lat.toString().toFloat())
                            storyLong?.add(maps[i].lon.toString().toFloat())
                        }
                    }
                    is MyResult.Error -> binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_map -> {
                mMap.mapType = MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_map -> {
                mMap.mapType = MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_map -> {
                mMap.mapType = MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_map -> {
                mMap.mapType = MAP_TYPE_HYBRID
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}