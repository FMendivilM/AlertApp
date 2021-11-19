package com.example.alertapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertapp.entities.User
import com.example.alertapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        binding.btnRegCreateAccount.setOnClickListener {
            val userName = binding.etRegUsername.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()
            val passwordConfirm = binding.etRegPasswordConfirm.text.toString().trim()

            if(TextUtils.isEmpty(userName)){
                binding.etRegUsername.error = "User name is required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)){
                binding.etRegEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etRegPassword.error = "Password must be 6 characters or longer"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)){
                binding.etRegPassword.error = "Password is required"
                return@setOnClickListener
            }

            if(!TextUtils.equals(password, passwordConfirm)){
                binding.etRegPassword.error = "Password mismatch"
                return@setOnClickListener
            }

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    val userId = fAuth.currentUser?.uid
                    val user = User(userId!!,userName,email,password)
                    val userData = HashMap<String, Any>()
                    userData["userName"] = user.getUserName()
                    userData["email"] = user.getEmail()
                    userData["password"] = user.getPassword()

                    dataBase.child("userData").child(userId).child("data").setValue(userData)
                    Toast.makeText(applicationContext, "User successfully registered", Toast.LENGTH_LONG).show()
                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvRegLogin.setOnClickListener{
            val i = Intent(applicationContext, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}