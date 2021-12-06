package com.example.alertapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.alertapp.databinding.ActivityFalseCallBinding

class FalseCallActivity : AppCompatActivity() {

    lateinit var binding: ActivityFalseCallBinding

    lateinit var defaultRingtone: Ringtone
    lateinit var vibrator: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFalseCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val canVibrate = intent.getBooleanExtra("canVibrate", false)
        val canSound = intent.getBooleanExtra("canSound", false)
        val name = intent.getStringExtra("name")

        binding.tvFalseCallName.text = name

        if(canVibrate){ vibrate() }
        if(canSound){ playRingtone() }

        binding.btnCallAccept.setOnClickListener{
            finish()
            if(canSound){ defaultRingtone.stop() }
            vibrator.cancel()

            val i = Intent(applicationContext, FalseCallReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)

        }
        binding.btnCallEnd.setOnClickListener{
            finish()
            if(canSound){ defaultRingtone.stop() }
            vibrator.cancel()
        }


    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            vibrator.vibrate(pattern,0)
        }
    }

    private fun playRingtone(){
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_RINGTONE)
        defaultRingtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        defaultRingtone.play()
    }


    }