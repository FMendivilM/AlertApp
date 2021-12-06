package com.example.alertapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertapp.databinding.ActivityAddContactBinding
import com.example.alertapp.entities.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class AddContactActivity : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var db : DatabaseReference

    var contact: Contact = Contact()
    lateinit var numberAux : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Agregar contacto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance()
        db = Firebase.database.reference

        val editing: Boolean = intent.getStringExtra("number") != null
        if(editing){
            supportActionBar?.title = "Editar contacto"
            binding.etAddContactName.setText(intent.getStringExtra("name"))
            binding.etAddContactNumber.setText(intent.getStringExtra("number"))
            binding.btnAddContact.text = "Save"
            numberAux = binding.etAddContactNumber.text.toString()
        }

        binding.btnAddContact.setOnClickListener{
            var contactInfo: HashMap<String, Any> = HashMap()

            if(TextUtils.isEmpty(binding.etAddContactName.text.toString())){
                binding.etAddContactNumber.error = "Name is required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(binding.etAddContactNumber.text.toString())){
              binding.etAddContactNumber.error = "Phone number is required"
              return@setOnClickListener
            }

            if(!TextUtils.isDigitsOnly(binding.etAddContactNumber.text.toString())){
                binding.etAddContactNumber.error = "Invalid phone number"
                return@setOnClickListener
            }

            if(binding.etAddContactNumber.text.toString().length != 10){
                binding.etAddContactNumber.error = "Phone number must be 10 characters long"
                return@setOnClickListener
            }

            contact.setContactName(binding.etAddContactName.text.toString())
            contact.setPhoneNumber(binding.etAddContactNumber.text.toString())

            contactInfo["name"] = contact.getContactName()
            contactInfo["number"] = contact.getPhoneNumber()

            db.child("userData").child(fAuth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists() && !editing){
                        for(objSnapshot: DataSnapshot in snapshot.child("contacts").children){
                            if(contact.getPhoneNumber() == objSnapshot.key){
                                binding.etAddContactNumber.error = "Phone number already in use"
                                return
                            }
                        }

                        if(editing){
                            db.child("userData").child(fAuth.currentUser!!.uid).child("contacts").child(numberAux).removeValue()
                        }

                        db.child("userData").child(fAuth.currentUser!!.uid).child("contacts").child(contact.getPhoneNumber()).setValue(contactInfo).addOnCompleteListener {task->
                            if(task.isSuccessful){
                                val message: String = if(editing){
                                    "Contact successfully changed"
                                }else{
                                    "Contact successfully created"
                                }

                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                finish()
                            }else{
                                Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        }

        binding.btnCancelContact.setOnClickListener{finish()}

    }
}