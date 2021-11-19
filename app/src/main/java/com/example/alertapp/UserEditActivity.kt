package com.example.alertapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertapp.databinding.ActivityUserEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserEditActivity : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var dataBase : DatabaseReference
    lateinit var binding : ActivityUserEditBinding

    private lateinit var userName : String
    private lateinit var mail : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        val userInfo: HashMap<String, Any> = HashMap()

        userName = intent.getStringExtra("userName")!!
        mail = intent.getStringExtra("email")!!
        password = intent.getStringExtra("password")!!

        binding.etEditUserName.setText(userName)
        binding.etEditUserEmail.setText(mail)
        binding.etEditUserPassword.setText(password)
        binding.etEditUserPasswordConfirm.setText(password)


        binding.btnUserUpdate.setOnClickListener {

            val userNameField = binding.etEditUserName.text.toString().trim()
            val emailField = binding.etEditUserEmail.text.toString().trim()
            val passwordField = binding.etEditUserPassword.text.toString().trim()
            val passwordConfirmField = binding.etEditUserPasswordConfirm.text.toString().trim()
            if(TextUtils.isEmpty(userNameField)){
                binding.etEditUserName.error = "User name is required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(emailField)){
                binding.etEditUserEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (passwordField.length < 6) {
                binding.etEditUserPassword.error = "Password must be 6 characters or longer"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(passwordField) || TextUtils.isEmpty(passwordConfirmField)){
                binding.etEditUserPassword.error = "Password is required"
                return@setOnClickListener
            }

            if(!TextUtils.equals(passwordField, passwordConfirmField)){
                binding.etEditUserPassword.error = "Password mismatch"
                return@setOnClickListener
            }

            userInfo["userName"] = binding.etEditUserName.text.toString()
            userInfo["email"] = binding.etEditUserEmail.text.toString()
            userInfo["password"] = binding.etEditUserPassword.text.toString()

            dataBase.child("userData").child(fAuth.currentUser!!.uid).child("data").updateChildren(userInfo)

            fAuth.currentUser!!.updateEmail(binding.etEditUserEmail.text.toString()).addOnCompleteListener{task->
                if(!task.isSuccessful){
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }

            fAuth.currentUser!!.updatePassword(binding.etEditUserPassword.text.toString()).addOnCompleteListener{task->
                if(!task.isSuccessful){
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
            finish()
        }

        binding.btnUserCancel.setOnClickListener { finish() }


    }
}