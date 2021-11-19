package com.example.alertapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.alertapp.fragments.AlertFragment
import com.example.alertapp.databinding.ActivityMainBinding
import com.example.alertapp.fragments.MapFragment
import com.example.alertapp.fragments.RouteFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference

    private val MY_PERMISSIONS_REQUEST_SEND_SMS: Int = 0
    private val MY_PERMISSIONS_REQUEST_CURRENT_LOCATION: Int = 1

    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_CURRENT_LOCATION)
        }


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)!=
            PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.SEND_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS)
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, AlertFragment.newInstance()).commit()


        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        binding.bottomNavigation.setOnItemSelectedListener{menuItem->
            lateinit var selectedFragment: Fragment

            when(menuItem.itemId){
                R.id.alert-> {
                    selectedFragment = AlertFragment.newInstance()
                }
                R.id.map->{
                    selectedFragment = MapFragment.newInstance()
                }
                R.id.route->{
                    selectedFragment = RouteFragment.newInstance()
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.container, selectedFragment).commit()

            return@setOnItemSelectedListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.dropdown_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.account->{
                val i = Intent(applicationContext, UserActivity::class.java)
                startActivity(i)
            }
            R.id.contacts->{
                val i = Intent(applicationContext, ContactListActivity::class.java)
                startActivity(i)
            }
            R.id.dropdown_alert->{
                val i = Intent(applicationContext, AlertConfigurationActivity:: class.java)
                startActivity(i)

            }
            R.id.false_call->{

            }
            R.id.log_out->{
                logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOut(){
        fAuth.signOut()
        val i = Intent(applicationContext, LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}
