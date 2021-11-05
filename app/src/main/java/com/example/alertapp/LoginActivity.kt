package com.example.alertapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth= FirebaseAuth.getInstance()

        if(fAuth.currentUser != null){
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
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
                }else{
                    Toast.makeText(applicationContext, task.exception?.message, Toast.LENGTH_LONG).show()
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