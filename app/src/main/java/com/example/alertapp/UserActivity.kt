package com.example.alertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.alertapp.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dataBase : DatabaseReference

    lateinit var binding: ActivityUserBinding
    lateinit var userName : String
    lateinit var mail : String
    lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dataBase = Firebase.database.reference

        binding.switchPassword.isChecked = true

        binding.switchPassword.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.tvPasswordField.transformationMethod = PasswordTransformationMethod.getInstance()
            }else{
                binding.tvPasswordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }
        loadData()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Eliminar usuario? Toda tu información se perderá")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dataBase.child("userData").child(fAuth.currentUser!!.uid).removeValue()
            fAuth.currentUser!!.delete().addOnCompleteListener{task->

                if (task.isSuccessful){
                    val i = Intent(applicationContext, LoginActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar"){dialog,_ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()

        binding.btnUserDelete.setOnClickListener{alert.show()}

        binding.btnUserEdit.setOnClickListener {
            val i = Intent(applicationContext, UserEditActivity::class.java)
            i.putExtra("userName", userName)
            i.putExtra("email", mail)
            i.putExtra("password", password)
            startActivity(i)
        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        dataBase.child("userData").child(fAuth.currentUser!!.uid).child("data").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                binding.tvUserName.text = snapshot.child("userName").value.toString()
                binding.tvEmailField.text = snapshot.child("email").value.toString()
                binding.tvPasswordField.text = snapshot.child("password").value.toString()

                userName = binding.tvUserName.text.toString()
                mail = binding.tvEmailField.text.toString()
                password = binding.tvPasswordField.text.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}