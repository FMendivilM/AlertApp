package com.example.alertapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.alertapp.databinding.FragmentAlertBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlertFragment : Fragment() {
    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var localGpsLocation: Location? = null
    private var localNetworkLocation: Location? = null

    private var latitude: Double = 0.00
    private var longitude: Double = 0.00

    private var canAccessLocation: Boolean = false
    private var canSendMessage: Boolean = false
    private var canMarkLocation: Boolean = false

    private var message:String = ""
    val contactNumberList = ArrayList<String>()

    var dateFormat =  SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)


    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference


    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlertBinding.inflate(inflater,container,false)

        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        @Suppress("DEPRECATION")
        val smsManager = SmsManager.getDefault()

        binding.btnAlert.setOnClickListener{

            if(hasGps || hasNetwork){
                if(hasGps){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F
                    ) { location -> locationGps = location }
                    localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(localGpsLocation != null){
                        locationGps = localGpsLocation
                    }
                }
                if(hasNetwork){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F
                    ){location -> locationNetwork = location}

                    localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if(localNetworkLocation != null){
                        locationNetwork = localNetworkLocation
                    }
                }
                if(locationNetwork != null && locationGps != null) {
                    if (locationGps!!.accuracy >= locationNetwork!!.accuracy) {
                        longitude = locationGps!!.longitude
                        latitude = locationGps!!.latitude

                    } else {
                        longitude = locationNetwork!!.longitude
                        latitude = locationNetwork!!.latitude
                    }

                }
            }else{
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }


            dataBase.child("userData").child(fAuth.currentUser!!.uid).child("myAlert").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        canAccessLocation = snapshot.child("locationPermission").value as Boolean
                        canMarkLocation = snapshot.child("markZonePermission").value as Boolean
                        canSendMessage = snapshot.child("messagePermission").value as Boolean
                    }

                    if(snapshot.child("contactsList").exists()){
                        for(i in 0.. snapshot.child("contactsList").childrenCount){
                            contactNumberList.add(snapshot.child("contactsList").child(i.toString()).child("phoneNumber").value.toString())
                        }

                    }

                    if(snapshot.child("message").exists()){
                        message = snapshot.child("message").value.toString()
                    }

                    if(canMarkLocation){
                        val alertInfo: HashMap<String, Any> = HashMap()
                        alertInfo["latitude"] = latitude
                        alertInfo["longitude"] = longitude
                        if(canSendMessage){
                            alertInfo["message"] = message
                        }
                        val calendar = Calendar.getInstance()
                        val date = calendar.time
                        val timeFormat : String = dateFormat.format(date)
                        alertInfo["time"] = timeFormat

                        dataBase.addListenerForSingleValueEvent(object: ValueEventListener{
                            @RequiresApi(Build.VERSION_CODES.S)
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var count = 0
                                if(snapshot.child("publicAlerts").exists()){
                                    count = snapshot.child("publicAlerts").childrenCount.toInt()
                                }

                                dataBase.child("publicAlerts").child(count.toString()).setValue(alertInfo).addOnCompleteListener{task->
                                    if(task.isSuccessful){
                                        vibrate()
                                        Toast.makeText(context, "Alert sent", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}

                        })
                    }

                    if(canSendMessage && ActivityCompat.checkSelfPermission(context as Context,
                        android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_DENIED){
                        if(contactNumberList.size > 0){
                            for(i in contactNumberList){
                                smsManager.sendTextMessage(i,
                                    null,
                                    "$message latitude: $latitude, longitude: $longitude",
                                    null,
                                    null
                                    )
                            }
                        }
                    }

                    if(!canMarkLocation && !canAccessLocation && !canSendMessage){
                        Toast.makeText(context, "Ve a configuración de alerta", Toast.LENGTH_LONG).show()
                    }
                }


                override fun onCancelled(error: DatabaseError) {}
            })

        }

        return binding.root
    }

    companion object {
        @JvmStatic fun newInstance() =
                AlertFragment().apply{}
    }
    @Suppress("DEPRECATION")
    private fun vibrate(){
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}