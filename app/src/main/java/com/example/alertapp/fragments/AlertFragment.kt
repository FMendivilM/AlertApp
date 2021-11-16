package com.example.alertapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.SettingInjectorService
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import com.example.alertapp.R
import com.example.alertapp.databinding.FragmentAlertBinding
class AlertFragment : Fragment() {
    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null


    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlertBinding.inflate(inflater,container,false)



        binding.btnAlert.setOnClickListener{
            locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(hasGps || hasNetwork){
                if(hasGps){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F
                    ) { location -> locationGps = location }

                    val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(localGpsLocation != null){
                        locationGps = localGpsLocation
                    }
                }

                if(hasNetwork){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F
                    ){location -> locationNetwork = location}

                    val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if(localNetworkLocation != null){
                        locationNetwork = localNetworkLocation
                    }
                }

                if(locationNetwork != null && locationGps != null) {
                    if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                        Toast.makeText(context, "${locationGps!!.latitude} ${locationGps!!.longitude}", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(context, "${locationNetwork!!.latitude} ${locationNetwork!!.longitude}", Toast.LENGTH_LONG).show()
                    }

                }
            }else{
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }


        return binding.root
    }

    companion object {
        @JvmStatic fun newInstance() =
                AlertFragment().apply{}
    }
}