package com.example.alertapp.fragments
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Criteria
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertapp.GoogleMapDTO
import com.example.alertapp.R
import com.example.alertapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.maps.route.extensions.getColor
import okhttp3.OkHttpClient
import okhttp3.Request


class MapFragment : Fragment() , OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    val MY_PERMISSIONS_REQUEST_CURRENT_LOCATION: Int = 1

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var routeMarker: Marker? = null

    private var routeLat : Double = 0.0
    private var routeLong : Double = 0.0

    private var poly : Polyline? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater,container,false)

        if (!Places.isInitialized()) {
            Places.initialize(context as Context, getString(R.string.api_key))
        }
        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        binding.fabRoute.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity as Activity, R.color.app_blue))

        mapView = binding.map
        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)

        binding.fabRoute.setOnClickListener{
            routeMarker?.remove()
            poly?.remove()

            if(binding.btnConfirmRoute.visibility == view?.visibility!!.xor(View.GONE)){

                binding.btnConfirmRoute.visibility = view?.visibility!!.xor(View.VISIBLE)
                binding.fabRoute.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity as Activity, R.color.app_red))
                binding.fabRoute.setImageResource(R.drawable.ic_cancel)


                routeMarker = map.addMarker(MarkerOptions()
                    .position(LatLng(updateLocation().latitude, updateLocation().longitude) )
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))!!

                routeLat = routeMarker?.position!!.latitude
                routeLong = routeMarker?.position!!.longitude
            }else{

                binding.btnConfirmRoute.visibility = view?.visibility!!.xor(View.GONE)
                binding.fabRoute.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity as Activity, R.color.app_blue))
                binding.fabRoute.setImageResource(R.drawable.ic_route)

                routeMarker = null
            }


        }


        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance()=
            MapFragment().apply{}
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override fun onMapReady(googleMap:GoogleMap) {
        MapsInitializer.initialize(requireContext())
        map = googleMap
        enableLocation()
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        alertList()

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(updateLocation(),15f))
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                routeLat = p0.position.latitude
                routeLong = p0.position.longitude
            }

            override fun onMarkerDragStart(p0: Marker) {
            }

        })



        binding.btnConfirmRoute.setOnClickListener{
            routeMarker?.isDraggable = false


            val url = getDirectionURL(updateLocation(), LatLng(routeLat, routeLong))
            GetDirection(url).cancel(true)
            GetDirection(url).execute()



            binding.btnConfirmRoute.visibility = view?.visibility!!.xor(View.GONE)
            binding.fabRoute.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity as Activity, R.color.app_blue))
            binding.fabRoute.setImageResource(R.drawable.ic_route)
        }

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
            MY_PERMISSIONS_REQUEST_CURRENT_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] ==
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
                        var lat = 0.00
                        var long = 0.00
                        if(snapshot.child(i.toString()).child("latitude").value is Double){
                            lat = snapshot.child(i.toString()).child("latitude").value as Double
                        }
                        if(snapshot.child(i.toString()).child("longitude").value is Double){
                            long = snapshot.child(i.toString()).child("longitude").value as Double
                        }else{
                            dataBase.child("publicAlerts").child(i.toString()).removeValue()
                        }
                        val message = snapshot.child(i.toString()).child("message").value.toString()
                        val time = snapshot.child(i.toString()).child("time").value.toString()
                        map.addMarker(MarkerOptions()
                            .position(LatLng(lat,long))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .title(time)
                            .snippet(message)
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }



    @SuppressLint("MissingPermission")
    private fun updateLocation() : LatLng{

        val criteria = Criteria()
        val activity = activity as Activity
        val locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(criteria, true)
        val location = locationManager.getLastKnownLocation(provider!!)
        val lat = location!!.latitude
        val long = location.longitude

        return LatLng(lat,long)
    }

    private fun getDirectionURL(origin: LatLng, destination: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDestination = "destination=${destination.latitude},${destination.longitude}"
        val parameters = "${strOrigin}&${strDestination}&driving"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=${getString(R.string.api_key)}"
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineOption = PolylineOptions()
            for (i in result.indices){
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(getColor(R.color.app_blue))
                lineOption.geodesic(true)
            }
            poly = map.addPolyline(lineOption)

        }
    }
}
