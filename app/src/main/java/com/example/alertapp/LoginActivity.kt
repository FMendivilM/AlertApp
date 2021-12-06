package com.example.alertapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alertapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private val MY_PERMISSIONS_REQUEST_SEND_SMS: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        fAuth= FirebaseAuth.getInstance()

        if(fAuth.currentUser != null){
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.SEND_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS)
        }

        binding.btnLogLogin.setOnClickListener {
            val email = binding.etLogEmail.text.toString().trim()
            val password = binding.etLogPassword.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                binding.etLogEmail.error = "Email is required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)){
                binding.etLogPassword.error = "Password is required"
                return@setOnClickListener
            }

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){task->
                if(task.isSuccessful) run {
                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                    Toast.makeText(applicationContext, "Sesión iniciada exitosamente", Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception?.message, Toast.LENGTH_LONG).show()
                    Log.e("a", task.exception?.message!!)
                }
            }
        }

        binding.tvLogRegister.setOnClickListener {
            val i = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }

    }
}