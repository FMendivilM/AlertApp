package com.example.alertapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alertapp.entities.FalseCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.internal.notify

class FalseCallReceiver : BroadcastReceiver() {

    lateinit var fAuth : FirebaseAuth
    lateinit var db : DatabaseReference



    override fun onReceive(context: Context?, intent: Intent?) {
        fAuth = FirebaseAuth.getInstance()
        db = Firebase.database.reference

        val falseCall = FalseCall()

        db.child("userData").child(fAuth.currentUser!!.uid).child("falseCall").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                falseCall.contactName = snapshot.child("name").value.toString()
                falseCall.canVibrate = snapshot.child("canVibrate").value as Boolean
                falseCall.canSound = snapshot.child("canSound").value as Boolean
                falseCall.canRepeat = snapshot.child("canRepeat").value as Boolean

                falseCall.activateTime = Integer.parseInt(snapshot.child("activateTime").value.toString())
                falseCall.timeOption = snapshot.child("timeOption").value.toString()

                val i = Intent(context, FalseCallActivity::class.java)
                i.putExtra("name", falseCall.contactName)
                i.putExtra("canVibrate", falseCall.canVibrate)
                i.putExtra("canSound", falseCall.canSound)
                i.putExtra("canRepeat", falseCall.canRepeat)
                i.putExtra("activateTime", falseCall.activateTime)
                i.putExtra("timeOption", falseCall.timeOption)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context!!.startActivity(i)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

}