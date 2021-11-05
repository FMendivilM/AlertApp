package com.example.alertapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alertapp.adapters.ContactAdapter
import com.example.alertapp.databinding.ActivityContactListBinding
import com.example.alertapp.entities.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContactListActivity : AppCompatActivity() {

    lateinit var contactAdapter: ContactAdapter
    lateinit var recyclerViewContacts: RecyclerView
    lateinit var contactList: ArrayList<Contact>
    lateinit var fAuth: FirebaseAuth
    lateinit var db : DatabaseReference
    lateinit var binding: ActivityContactListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        db = Firebase.database.reference

        loadList()

        binding.btnAddContact.setOnClickListener{
            val i = Intent(applicationContext, AddContactActivity::class.java)
            startActivity(i)
        }

    }

    override fun onResume() {
        super.onResume()
        loadList()
    }



    fun showData(){
        recyclerViewContacts.layoutManager = LinearLayoutManager(this@ContactListActivity)
        contactAdapter = ContactAdapter(this@ContactListActivity, contactList)
        recyclerViewContacts.adapter = contactAdapter

        contactAdapter.setOnClickListener{v->
            val i = Intent(this, ContactViewActivity::class.java)
            i.putExtra("contactNumber", contactList[recyclerViewContacts.getChildAdapterPosition(v)].getPhoneNumber())
            startActivity(i)
        }
    }

    private fun loadList(){
        db.child("userData").child(fAuth.currentUser!!.uid).child("contacts").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                for(objSnapshot:DataSnapshot in snapshot.children){
                    contactList.add(
                        Contact(
                        objSnapshot.child("name").toString(),
                            objSnapshot.child("number").toString())
                    )
                }

                if(contactList.size > 0){
                    binding.tvEmptyListContacts.text = ""
                }else{
                    binding.tvEmptyListContacts.text = "No registered contacts"
                }
                showData()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}