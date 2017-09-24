package com.sofps.inspirationalquotes.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.widget.TextView;
import com.sofps.inspirationalquotes.R;

public class AppRater {

	private final static int DAYS_UNTIL_PROMPT = 2;
	private final static int LAUNCHES_UNTIL_PROMPT = 3;

	private final static String PREF_APP_RATER = "apprater";
	private final static String PREF_DONT_SHOW_AGAIN = "dontshowagain";
	private final static String PREF_LAUNCH_COUNT = "launch_count";
	private final static String PREF_FIRST_LAUNCH = "date_firstlaunch";

	public static void app_launched(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(PREF_APP_RATER,
				0);
		if (prefs.getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong(PREF_LAUNCH_COUNT, 0) + 1;
		editor.putLong(PREF_LAUNCH_COUNT, launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong(PREF_FIRST_LAUNCH, 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(PREF_FIRST_LAUNCH, date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch
					+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, editor);
			}
		}

		editor.commit();
	}

	@SuppressLint("NewApi")
	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		Resources res = mContext.getResources();
		String title = String.format(res.getString(R.string.rate_title),
				res.getString(R.string.app_name));

		TextView textView = new TextView(mContext);
		String text = String.format(res.getString(R.string.rate_msg),
				res.getString(R.string.app_name));
		textView.setText(text);
		float size = res.getDimension(R.dimen.text_size)
				/ res.getDisplayMetrics().density;
		textView.setTextSize(size);
		textView.setPadding(10, 10, 10, 10);

		AlertDialog dialog;
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(mContext,
					R.style.CustomDialogTheme);
		} else {
			builder = new AlertDialog.Builder(mContext);
		}
		builder.setView(textView)
				.setTitle(title)
				.setIcon(res.getDrawable(R.drawable.ic_launcher))
				.setPositiveButton(R.string.rate_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mContext.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("amzn://apps/android?p="
												+ mContext.getPackageName())));
								if (editor != null) {
									editor.putBoolean(PREF_DONT_SHOW_AGAIN,
											true);
									editor.commit();
								}
								dialog.dismiss();
							}
						})
				.setNeutralButton(R.string.remind_later_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.dont_rate_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (editor != null) {
									editor.putBoolean(PREF_DONT_SHOW_AGAIN,
											true);
									editor.commit();
								}
								dialog.dismiss();
							}
						});
		dialog = builder.create();
		dialog.show();
	}

}
