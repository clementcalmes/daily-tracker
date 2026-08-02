package com.medtracker.app

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "med_tracker_prefs"
    private const val KEY_HOUR = "reminder_hour"
    private const val KEY_MINUTE = "reminder_minute"
    private const val KEY_LAST_VALIDATED = "last_validated_date"
    private const val KEY_HISTORY = "history_dates"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getHour(context: Context): Int = prefs(context).getInt(KEY_HOUR, 20)
    fun getMinute(context: Context): Int = prefs(context).getInt(KEY_MINUTE, 0)

    fun setTime(context: Context, hour: Int, minute: Int) {
        prefs(context).edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }

    fun getLastValidatedDate(context: Context): String? =
        prefs(context).getString(KEY_LAST_VALIDATED, null)

    fun setValidatedToday(context: Context, dateStr: String) {
        prefs(context).edit()
            .putString(KEY_LAST_VALIDATED, dateStr)
            .apply()
        addToHistory(context, dateStr)
    }

    private fun addToHistory(context: Context, dateStr: String) {
        val history = getHistory(context).toMutableSet()
        history.add(dateStr)
        prefs(context).edit()
            .putStringSet(KEY_HISTORY, history)
            .apply()
    }

    fun getHistory(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_HISTORY, emptySet()) ?: emptySet()

    fun isValidatedToday(context: Context, todayStr: String): Boolean =
        getLastValidatedDate(context) == todayStr
}
