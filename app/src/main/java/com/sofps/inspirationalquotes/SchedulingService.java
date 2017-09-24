package com.sofps.inspirationalquotes;

import com.sofps.inspirationalquotes.ui.ScreenSlideActivity;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the app's actual work. {@code AlarmReceiver}
 * (a {@code WakefulBroadcastReceiver}) holds a partial wake lock for this
 * service while the service does its work. When the service is finished, it
 * calls {@code completeWakefulIntent()} to release the wake lock.
 */
public class SchedulingService extends IntentService {
	private static final String TAG = "SchedulingService";

	public static final int NOTIFICATION_ID = 1;

	public SchedulingService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");

		// Send Notification only when app is not running in the foreground
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(1);
		if (!services.get(0).topActivity.getPackageName().equalsIgnoreCase(
				getPackageName())) {
			Log.d(TAG, "Sending notification");
			sendNotification(getString(R.string.notification_msg));
		} else {
			Log.d(TAG,
					"NOT Sending notification, app running in the foreground");
		}

		// Release the wake lock provided by the BroadcastReceiver.
		AlarmReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "sendNotification");

		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Intent triggered
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ScreenSlideActivity.class), 0);

		// Define sound URI, the sound to be played when there's a notification
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(msg).setSound(soundUri);
		mBuilder.setContentIntent(contentIntent);

		Notification notification = mBuilder.build();

		// Hide the notification after it was selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

}
