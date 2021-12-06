package com.example.alertapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.alertapp.databinding.ActivityFalseCallOptionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FalseCallOptionsActivity : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var db : DatabaseReference
    lateinit var binding : ActivityFalseCallOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFalseCallOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Llamada falsa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance()
        db = Firebase.database.reference
        fillSpinner()

        binding.btnSetFalseCall.setOnClickListener{
            if(TextUtils.isEmpty(binding.etFalseCallName.text.toString())){
                binding.etFalseCallName.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }


            if(TextUtils.isEmpty(binding.etTime.text.toString())){
                binding.etTime.error = "El campo no puede estar vacío"
                return@setOnClickListener
            }
            if(!TextUtils.isDigitsOnly(binding.etTime.text.toString())){
                binding.etTime.error = "Valor inválido"
                return@setOnClickListener
            }

            val falseCallInfo: HashMap<String, Any> = HashMap()
            falseCallInfo["name"] = binding.etFalseCallName.text.toString()
            falseCallInfo["canVibrate"] = binding.cbVibration.isChecked
            falseCallInfo["canSound"] = binding.cbSound.isChecked
            falseCallInfo["canRepeat"] = binding.cbRepeat.isChecked
            falseCallInfo["activateTime"] = binding.etTime.text.toString()
            falseCallInfo["timeOption"] = binding.spinner.selectedItem.toString()

            db.child("userData").child(fAuth.currentUser!!.uid).child("falseCall").setValue(falseCallInfo).addOnCompleteListener{
                task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Llamada falsa creada con éxito", Toast.LENGTH_LONG).show()
                    createFalseCall()
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    private fun fillSpinner(){
        val timeOptions = ArrayAdapter.createFromResource(applicationContext, R.array.falseCallSpinner, android.R.layout.simple_spinner_item)
        timeOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = timeOptions
    }

    private fun createFalseCall(){
        val i = Intent(applicationContext, FalseCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        var timeLong : Long = 0
        when(binding.spinner.selectedItem.toString()){
            "Segundos"->{
                timeLong = (Integer.parseInt(binding.etTime.text.toString()) * 1000).toLong()
            }
            "Minutos"->{
                timeLong = (Integer.parseInt(binding.etTime.text.toString()) * 1000 * 60).toLong()
            }
            "Horas"->{
                timeLong = (Integer.parseInt(binding.etTime.text.toString()) * 1000 * 60 * 60).toLong()
            }
        }

        if(binding.cbRepeat.isChecked){
            alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + timeLong, timeLong, pendingIntent)
        }else{
            alarmManager?.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + timeLong, pendingIntent)
        }
    }
}