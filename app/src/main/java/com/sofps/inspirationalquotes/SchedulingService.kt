package com.sofps.inspirationalquotes

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sofps.inspirationalquotes.ui.MainActivity

/**
 * This `IntentService` does the app's actual work. `AlarmReceiver`
 * (a `WakefulBroadcastReceiver`) holds a partial wake lock for this
 * service while the service does its work. When the service is finished, it
 * calls `completeWakefulIntent()` to release the wake lock.
 */
class SchedulingService : IntentService(TAG) {

    companion object {
        private const val TAG = "SchedulingService"

        const val NOTIFICATION_ID = 1
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent")

        // Send Notification only when app is not running in the foreground
        if (!appInForeground(this)) {
            Log.d(TAG, "Sending notification")
            sendNotification(getString(R.string.notification_msg))
        } else {
            Log.d(TAG, "NOT Sending notification, app running in the foreground")
        }

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent!!)
    }

    private fun appInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any {
            it.processName == context.packageName &&
                    it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }

    private fun sendNotification(msg: String) {
        Log.d(TAG, "sendNotification")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent triggered
        val contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                0)

        // Define sound URI, the sound to be played when there's a notification
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msg).setSound(soundUri)
                .setContentIntent(contentIntent)
                .build().also {
                    // Hide the notification after it was selected
                    it.flags = it.flags or Notification.FLAG_AUTO_CANCEL
                }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}
