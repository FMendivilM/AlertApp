package com.example.alertapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout.LayoutParams
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alertapp.databinding.ActivityAlertConfigurationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AlertConfigurationActivity : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference
    private lateinit var binding: ActivityAlertConfigurationBinding
    val contactList = ArrayList<String>()
    val contactNumberList = ArrayList<String>()
    val spinnerList = ArrayList<Spinner>()
    private var canSendMessage = true
    private var canAccessLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference


        if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            binding.checkBoxLocation.isEnabled = false
            binding.checkBoxLocation.setTextColor(Color.GRAY)

            binding.checkBoxZoneMark.isEnabled = false
            binding.checkBoxZoneMark.setTextColor(Color.GRAY)
            canAccessLocation = false
        }

        if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            binding.checkBoxMessage.isEnabled = false
            binding.checkBoxMessage.setTextColor(Color.GRAY)

            binding.editTextMessage.isEnabled = false
            binding.textViewAddContact.isEnabled = false
            binding.textViewAddContact.setTextColor(Color.GRAY)
            canSendMessage = false
        }
        if(!canAccessLocation && !canSendMessage){
            binding.btnSave.isEnabled = false
            binding.btnSave.setBackgroundColor(Color.GRAY)
            Toast.makeText(applicationContext, "Activa los permisos de ubicación y mensajes", Toast.LENGTH_LONG).show()
        }

        dataBase.child("userData").child(fAuth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("myAlert").exists()){
                    val objSnapshot = snapshot.child("myAlert")
                    if(objSnapshot.child("message").exists()){
                        binding.editTextMessage.setText(objSnapshot.child("message").value.toString())
                    }
                    binding.checkBoxLocation.isChecked = objSnapshot.child("locationPermission").value as Boolean
                    binding.checkBoxZoneMark.isChecked = objSnapshot.child("locationPermission").value as Boolean

                    binding.checkBoxMessage.isChecked = objSnapshot.child("messagePermission").value as Boolean


                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        binding.textViewAddContact.setOnClickListener{
            addSpinner()
        }

        binding.btnSave.setOnClickListener {
            dataBase.child("userData").child(fAuth.currentUser!!.uid).child("myAlarm").setValue(contactNumberList)

        }

    }

    private fun addSpinner(){
        val spinner = Spinner(this)
        spinner.setBackgroundResource(R.drawable.round_spinner)

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(10,10,10,10)
        fillSpinner(spinner)
        binding.linearlContacts.addView(spinner, layoutParams)
        spinnerList.add(spinner)
    }

    private fun fillSpinner(selectedSpinner: Spinner){

        lateinit var contactName:String
        lateinit var contactNumber: String
        contactList.clear()
        contactNumberList.clear()
        contactList.add("None")
        contactNumberList.add("")

        dataBase.child("userData").child(fAuth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("contacts").exists()){
                    for(objSnapshot: DataSnapshot in snapshot.child("contacts").children){
                        contactName = objSnapshot.child("name").value.toString()
                        contactNumber = objSnapshot.child("number").value.toString()
                        if(!contactList.contains(contactName)){
                            contactList.add(contactName)
                        }
                        if(!contactNumberList.contains(contactNumber)){
                            contactNumberList.add(contactNumber)
                        }


                    }
                }
                val alertContacts = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, contactList)
                alertContacts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectedSpinner.adapter = alertContacts

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}