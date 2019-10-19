package com.sofps.inspirationalquotes

import java.util.Calendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.legacy.content.WakefulBroadcastReceiver
import android.util.Log

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast
 * Intent and then starts the IntentService `SampleSchedulingService` to
 * do some work.
 */
class AlarmReceiver : WakefulBroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"

        fun completeWakefulIntent(intent: Intent) = WakefulBroadcastReceiver.completeWakefulIntent(intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        val service = Intent(context, SchedulingService::class.java)

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service)
    }

    /**
     * Sets a repeating alarm that runs once a day at approximately 10:00 a.m.
     * When the alarm fires, the app broadcasts an Intent to this
     * WakefulBroadcastReceiver.
     *
     * @param context
     */
    fun setAlarm(context: Context) {
        Log.d(TAG, "setAlarm")

        // The app's AlarmManager, which provides access to the system alarm
        // services
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        // The pending intent that is triggered when the alarm fires
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            // Set the alarm's trigger time to 10:00 a.m.
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }

        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()

        if (calendar.before(calNow)) {
            calendar.add(Calendar.DATE, 1)
        }
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent)

        // Enable {@code BootReceiver} to automatically restart the alarm
        // when the device is rebooted.
        context.packageManager.apply {
            setComponentEnabledSetting(ComponentName(context, BootReceiver::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
        }
    }

    /**
     * Cancels the alarm.
     *
     * @param context
     */
    fun cancelAlarm(context: Context) {
        Log.d(TAG, "cancelAlarm")

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // If the alarm has been set, cancel it.
        Log.d(TAG, "Cancelling alarm")
        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmMgr.cancel(alarmIntent)

        // Disable {@code BootReceiver} so that it doesn't automatically
        // restart the alarm when the device is rebooted.
        context.packageManager.apply {
            setComponentEnabledSetting(ComponentName(context, BootReceiver::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP)
        }

    }

}
