package com.example.mad_practical_4_22012011101

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad_practical_4_2201101.AlarmBroadcastReceiver
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var createAlarmButton: Button
    private lateinit var cancelAlarmButton: Button
    private lateinit var alarmTimeTextView: TextView
    private lateinit var stopAlarmButton: Button
    private lateinit var pendingIntent: PendingIntent
    private var alarmManager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        createAlarmButton = findViewById(R.id.create_alarm_button)
        cancelAlarmButton = findViewById(R.id.cancel_alarm_button)
        alarmTimeTextView = findViewById(R.id.alarm_time_textview)
        stopAlarmButton = findViewById(R.id.stop_alarm_button)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set OnClickListener for createAlarmButton to open the timer dialog
        createAlarmButton.setOnClickListener {
            showTimerDialog()
        }

        // Set OnClickListener for cancelAlarmButton to cancel the alarm before it rings
        cancelAlarmButton.setOnClickListener {
            cancelAlarm()
        }

        // Set OnClickListener for stopAlarmButton to stop the alarm when it rings
        stopAlarmButton.setOnClickListener {
            stopAlarm()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun showTimerDialog() {
        val cldr: Calendar = Calendar.getInstance()
        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = cldr.get(Calendar.MINUTE)
        val picker = TimePickerDialog(
            this,
            { _, sHour, sMinute ->
                val millisTime = calculateMillisFromTime(sHour, sMinute)
                setAlarm(millisTime)
            },
            hour, minutes, false
        )
        picker.show()
    }

    fun calculateMillisFromTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    @SuppressLint("MissingPermission", "ScheduleExactAlarm")
    fun setAlarm(millisTime: Long) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent)

        // Display the alarm time and show the Cancel button
        alarmTimeTextView.text = "Alarm set for: " + SimpleDateFormat("hh:mm a").format(millisTime)
        alarmTimeTextView.visibility = TextView.VISIBLE
        cancelAlarmButton.visibility = Button.VISIBLE
        stopAlarmButton.visibility = Button.GONE // Hide stop button until the alarm rings

        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
    }

    fun cancelAlarm() {
        if (::pendingIntent.isInitialized) {
            alarmManager?.cancel(pendingIntent)
            Toast.makeText(this, "Alarm canceled", Toast.LENGTH_LONG).show()

            // Reset UI after alarm cancellation
            alarmTimeTextView.visibility = TextView.GONE
            cancelAlarmButton.visibility = Button.GONE
            stopAlarmButton.visibility = Button.GONE
        }
    }

    fun stopAlarm() {
        if (::pendingIntent.isInitialized) {
            // Stop the alarm sound through AlarmBroadcastReceiver
            val intent = Intent(this, AlarmBroadcastReceiver::class.java)
            sendBroadcast(intent) // This will call onReceive() in AlarmBroadcastReceiver

            Toast.makeText(this, "Alarm stopped", Toast.LENGTH_LONG).show()

            // Reset UI after stopping the alarm
            alarmTimeTextView.visibility = TextView.GONE
            cancelAlarmButton.visibility = Button.GONE
            stopAlarmButton.visibility = Button.GONE
        }
    }
}
