package com.sofps.inspirationalquotes;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast
 * Intent and then starts the IntentService {@code SampleSchedulingService} to
 * do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
	private final static String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");

		Intent service = new Intent(context, SchedulingService.class);

		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, service);
	}

	/**
	 * Sets a repeating alarm that runs once a day at approximately 10:00 a.m.
	 * When the alarm fires, the app broadcasts an Intent to this
	 * WakefulBroadcastReceiver.
	 * 
	 * @param context
	 */
	public void setAlarm(Context context) {
		Log.d(TAG, "setAlarm");

		// The app's AlarmManager, which provides access to the system alarm
		// services
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);

		// The pending intent that is triggered when the alarm fires
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		// Set the alarm's trigger time to 10:00 a.m.
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 00);

		Calendar calNow = Calendar.getInstance();
		calNow.setTimeInMillis(System.currentTimeMillis());

		if (calendar.before(calNow)) {
			calendar.add(Calendar.DATE, 1);
		}
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				alarmIntent);

		// Enable {@code BootReceiver} to automatically restart the alarm
		// when the
		// device is rebooted.
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	/**
	 * Cancels the alarm.
	 * 
	 * @param context
	 */
	public void cancelAlarm(Context context) {
		Log.d(TAG, "cancelAlarm");

		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// If the alarm has been set, cancel it.
		if (alarmMgr != null) {
			Log.d(TAG, "Cancelling alarm");
			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,
					intent, 0);
			alarmMgr.cancel(alarmIntent);
		}

		// Disable {@code BootReceiver} so that it doesn't automatically
		// restart the
		// alarm when the device is rebooted.
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

}
