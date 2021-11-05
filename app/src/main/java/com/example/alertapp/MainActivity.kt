package com.example.alertapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

    val MY_PERMISSIONS_REQUEST_SEND_SMS: Int = 0

    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

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

            }
            R.id.contacts->{
                val i = Intent(applicationContext, ContactListActivity::class.java)
                startActivity(i)
            }
            R.id.dropdown_alert->{

            }
            R.id.false_call->{

            }
            R.id.log_out->{

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
