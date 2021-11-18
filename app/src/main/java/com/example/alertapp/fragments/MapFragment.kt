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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.jar.Manifest


class MapFragment : Fragment() , OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var mView: View
    val MY_PERMISSIONS_REQUEST_CURRENT_LOCATION: Int = 1

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       mView = inflater.inflate(R.layout.fragment_map, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(context as Context, getString(R.string.api_key))

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

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
        alertList()

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

    private fun alertList(){
        dataBase.child("publicAlerts").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(i in 0 until snapshot.childrenCount){
                        val lat = snapshot.child(i.toString()).child("latitude").value as Double
                        val long = snapshot.child(i.toString()).child("longitude").value as Double
                        createMapMarker(lat,long)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}