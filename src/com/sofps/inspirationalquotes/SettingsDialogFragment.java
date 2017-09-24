package com.sofps.inspirationalquotes;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsDialogFragment extends DialogFragment {
	private static final String TAG = "SettingsDialogFragment";

	public static final String PREF_NOTIFICATION_ENABLED = "notificationEnabled";
	public static final String PREF_LANGUAGE = "language";

	public SettingsDialogFragment() {
	}

	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog");
		final boolean notifEnabled = PreferenceManager
				.getDefaultSharedPreferences(getActivity()).getBoolean(
						PREF_NOTIFICATION_ENABLED, true);

		final String languageSelected = PreferenceManager
				.getDefaultSharedPreferences(getActivity()).getString(
						PREF_LANGUAGE, null);
		Log.d(TAG, languageSelected);

		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(getActivity(),
					R.style.CustomDialogTheme);
		} else {
			builder = new AlertDialog.Builder(getActivity());
		}

//		builder = new AlertDialog.Builder(new ContextThemeWrapper(
//				getActivity(), R.style.CustomDialogTheme));

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_settings, null);
		final View enableNotifications;
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			enableNotifications = view.findViewById(R.id.enable_notifications);
			((Switch) enableNotifications).setChecked(notifEnabled);
		} else {
			enableNotifications = view.findViewById(R.id.enable_notifications);
			((CheckBox) enableNotifications).setChecked(notifEnabled);
		}

		final Spinner spinner = (Spinner) view
				.findViewById(R.id.language_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.language_labels,
				R.layout.spinner_textview);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// Select current language
		int position = Arrays.asList(
				getResources().getStringArray(R.array.language_values))
				.indexOf(languageSelected);
		spinner.setSelection(position);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				.setTitle(R.string.action_settings)
				.setIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Check if notifications settings were changed
								boolean currentValue;
								if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
									currentValue = ((Switch) enableNotifications)
											.isChecked();
								} else {
									currentValue = ((CheckBox) enableNotifications)
											.isChecked();
								}
								if (currentValue != notifEnabled) {
									// If value was changed, persist
									PreferenceManager
											.getDefaultSharedPreferences(
													getActivity())
											.edit()
											.putBoolean(
													PREF_NOTIFICATION_ENABLED,
													currentValue).commit();
								}

								// Check if language was changed
								String currentLang = getResources()
										.getStringArray(R.array.language_values)[spinner
										.getSelectedItemPosition()];
								if (!currentLang.equals(languageSelected)) {
									PreferenceManager
											.getDefaultSharedPreferences(
													getActivity())
											.edit()
											.putString(PREF_LANGUAGE,
													currentLang).commit();
								}
							}
						})
				.setNegativeButton(R.string.button_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Do nothing
							}
						});
		return builder.create();

	}
}
