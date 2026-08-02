package com.medtracker.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(Date())

        if (!Prefs.isValidatedToday(context, today)) {
            NotificationHelper.showReminder(context)
            AlarmScheduler.scheduleSnooze(context, 10)
        }
    }
}
