package com.example.alertapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertapp.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import java.util.jar.Manifest


class MapFragment : Fragment() , OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var mView: View
    val MY_PERMISSIONS_REQUEST_CURRENT_LOCATION: Int = 1



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       mView = inflater.inflate(R.layout.fragment_map, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(context as Context, getString(R.string.api_key))

        mapView = mView.findViewById(R.id.map) as MapView
        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    companion object {
        @JvmStatic
        fun newInstance()=
            MapFragment().apply{}
        }

    override fun onMapReady(googleMap:GoogleMap) {
       MapsInitializer.initialize(requireContext())
        map = googleMap
        enableLocation()
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        createMapMarker(24.057729,-110.2951773)

    }

    private fun createMapMarker(lat: Double, long: Double,){
        val position = LatLng(lat,long)
        map.addMarker(MarkerOptions().position(position))
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(context as Context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::map.isInitialized){
            return
        }
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(context, "Ve a configuración y activa los permisos", Toast.LENGTH_LONG).show()
        }else{
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_CURRENT_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSIONS_REQUEST_CURRENT_LOCATION -> if(!grantResults.isEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(context as Context, "Para activar la localización ve a ajustes y activa los permisos", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(context as Context, "Para activar la localización ve a ajustes y activa los permisos", Toast.LENGTH_LONG).show()
        }
    }

    private fun createPolyLines(){
    }
}