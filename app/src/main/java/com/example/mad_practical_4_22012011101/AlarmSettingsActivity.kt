package com.example.mad_practical_4_22012011101

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_practical_4_2201101.AlarmBroadcastReceiver
import java.util.Calendar

class AlarmSettingsActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var selectedTimeTextView: TextView

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_settings)
//        setContentView(R.layout.activity_alarm_settings)

        // Find views by ID with safe null checks
        timePicker = findViewById<TimePicker>(R.id.time_picker)
        selectedTimeTextView = findViewById<TextView>(R.id.selected_time_text)

        // Handle potential null views
        // Set up listeners and click handlers
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            selectedTimeTextView.text = selectedTime
        }

        val setAlarmButton = findViewById<Button>(R.id.set_alarm_button)
        setAlarmButton.setOnClickListener {
            setAlarm(selectedTimeTextView.text.toString())
            finish() // Close the activity after setting the alarm
        }

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton?.setOnClickListener {
            finish() // Close the activity
        }
    }

    @SuppressLint("MissingPermission", "ScheduleExactAlarm") // Warning: Check permissions before using AlarmManager
    fun setAlarm(selectedTime: String) {
        // Parse the selected time string into hours and minutes
        val timeParts = selectedTime.split(":")
        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()

        // Create a Calendar object and set the alarm time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Check if the set time has already passed for today
        if (calendar.before(Calendar.getInstance())) {
            // If it has passed, set the alarm for tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val millisTime = calendar.timeInMillis

        // Create an Intent to trigger the alarm
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)

        // Create a PendingIntent to start the alarm
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Get the AlarmManager service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent)

        Toast.makeText(this, "Alarm set for $selectedTime", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 101 // Request code for permission
    }

    // Handle permission request result (optional)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // ... (unchanged)
    }
}