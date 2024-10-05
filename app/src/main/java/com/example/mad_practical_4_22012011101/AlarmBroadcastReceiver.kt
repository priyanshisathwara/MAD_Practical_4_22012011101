package com.example.mad_practical_4_2201101

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import com.example.mad_practical_4_22012011101.R

class AlarmBroadcastReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        // Use vibration if the permission is granted
        if (context.checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            val vibrator = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                vibrator.vibrate(4000)
            }
        } else {
            Toast.makeText(context, "Vibration permission not granted", Toast.LENGTH_SHORT).show()
        }

        // Show alarm notification (or message)
        val message = "Alarm! Wake up! Wake up!"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        // Play the alarm sound
        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer?.start()  // Play alarm sound

        // Add logic to stop the alarm sound based on user interaction
        // (for example, an activity can interact with this BroadcastReceiver)
    }

    // Function to stop the alarm
    fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
