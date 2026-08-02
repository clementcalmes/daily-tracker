package com.medtracker.app

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medtracker.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTimeLabel()

        binding.btnChangeTime.setOnClickListener {
            val hour = Prefs.getHour(this)
            val minute = Prefs.getMinute(this)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                Prefs.setTime(this, selectedHour, selectedMinute)
                AlarmScheduler.cancel(this)
                AlarmScheduler.scheduleDaily(this)
                updateTimeLabel()
            }, hour, minute, true).show()
        }
    }

    private fun updateTimeLabel() {
        val hour = Prefs.getHour(this)
        val minute = Prefs.getMinute(this)
        binding.tvCurrentTime.text = String.format("Heure actuelle du rappel : %02d:%02d", hour, minute)
    }
}
