package com.sofps.inspirationalquotes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SampleBootReceiver"
    }

    private var alarm = AlarmReceiver()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            alarm.setAlarm(context)
        }
    }
}
