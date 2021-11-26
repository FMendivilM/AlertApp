package com.example.alertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.alertapp.databinding.ActivityContactViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContactViewActivity : AppCompatActivity() {

    private lateinit var number: String
    private lateinit var fAuth: FirebaseAuth
    private lateinit var db : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContactViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        db = Firebase.database.reference

        if(intent.getStringExtra("contactNumber") != null){
            number = intent.getStringExtra("contactNumber")!!
        }

        db.child("userData").child(fAuth.currentUser!!.uid).child("contacts").child(number).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    binding.tvContactName.text = snapshot.child("name").value.toString()
                    binding.tvNumber.text = snapshot.child("number").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        binding.btnContactViewEdit.setOnClickListener{
            val i = Intent(applicationContext, AddContactActivity::class.java)
            i.putExtra("name", binding.tvContactName.text.toString())
            i.putExtra("number", binding.tvNumber.text.toString())
            startActivity(i)
            finish()
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Eliminar contacto?")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            number = binding.tvNumber.text.toString()
            db.child("userData").child(fAuth.currentUser!!.uid).child("contacts").child(number).removeValue()
            dialog.dismiss()
            finish()
        }

        builder.setNegativeButton("Cancelar"){dialog,_ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()

        binding.btnContactViewDelete.setOnClickListener{alert.show()}
    }
}