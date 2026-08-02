package com.medtracker.app

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.medtracker.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)

    private val notifPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { /* résultat ignoré, on continue dans tous les cas */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermissionIfNeeded()
        checkExactAlarmPermission()

        binding.btnValidate.setOnClickListener {
            validateToday()
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        AlarmScheduler.scheduleDaily(this)
        refreshUI()
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    private fun validateToday() {
        val today = dateFormat.format(Date())
        Prefs.setValidatedToday(this, today)
        NotificationHelper.cancelReminder(this)
        AlarmScheduler.cancel(this)
        AlarmScheduler.scheduleDaily(this)
        refreshUI()
    }

    private fun refreshUI() {
        val today = dateFormat.format(Date())
        val validatedToday = Prefs.isValidatedToday(this, today)

        binding.btnValidate.isEnabled = !validatedToday
        binding.btnValidate.text = if (validatedToday) "Déjà validé aujourd'hui ✓" else "Valider aujourd'hui"
        binding.tvStatus.text = if (validatedToday)
            "Bravo, c'est fait pour aujourd'hui !"
        else
            "Pas encore validé aujourd'hui"

        buildHistoryGrid()
    }

    private fun buildHistoryGrid() {
        binding.historyGrid.removeAllViews()
        val history = Prefs.getHistory(this)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -29)

        for (i in 0 until 30) {
            val dateStr = dateFormat.format(calendar.time)
            val dot = android.widget.TextView(this).apply {
                text = if (history.contains(dateStr)) "●" else "○"
                textSize = 20f
                setPadding(12, 12, 12, 12)
                setTextColor(
                    if (history.contains(dateStr))
                        ContextCompat.getColor(context, android.R.color.holo_green_dark)
                    else
                        ContextCompat.getColor(context, android.R.color.darker_gray)
                )
            }
            binding.historyGrid.addView(dot)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}
