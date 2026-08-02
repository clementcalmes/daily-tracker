package com.medtracker.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val minutes = intent.getIntExtra("minutes", 5)
        NotificationHelper.cancelReminder(context)
        AlarmScheduler.scheduleSnooze(context, minutes)
    }
}
