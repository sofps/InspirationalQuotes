package com.sofps.inspirationalquotes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.sofps.inspirationalquotes.AlarmReceiver;
import com.sofps.inspirationalquotes.Injection;
import com.sofps.inspirationalquotes.R;
import com.sofps.inspirationalquotes.asynctask.ScreenshotLoader;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.QuotesService;
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource;
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource;
import com.sofps.inspirationalquotes.util.LanguagePreferences;
import com.sofps.inspirationalquotes.util.ScreenshotUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
		ScreenshotLoader.ScreenshotLoaderTaskListener,
		SharedPreferences.OnSharedPreferenceChangeListener,
		LanguagePreferences.LanguagePreferencesListener {

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.loader) LottieAnimationView loader;
	@BindView(R.id.container) FrameLayout mContainer;

	public static final String PREF_NOTIFICATION_ENABLED = "notificationEnabled";

	private static final String TAG = "MainActivity";

	private static final String ALARM_SET = "alarm_set";
	private static final String LANGUAGE = "language";

	private boolean mAlarmSet;

	private SharedPreferences mPreferences;
	private LanguagePreferences mLanguagePreferences;

	private QuotesSlideFragment mQuotesSlideFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		loadToolbar();
		loadPreferences();
		loadAlarm(savedInstanceState);

		if (savedInstanceState == null) {
			mQuotesSlideFragment = new QuotesSlideFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mQuotesSlideFragment)
					.commit();
		}

		// TODO not sure if we need a reference to the presenter here
		DataBaseHelper dataBaseHelper = Injection.provideDataBaseHelper(getApplicationContext());
		QuotesLocalDataSource quotesLocalDataSource = Injection.provideQuotesLocalDataSource(dataBaseHelper);
		QuotesService quotesService = Injection.provideQuotesService();
		QuotesRemoteDataSource quotesRemoteDataSource = Injection.provideQuotesRemoteDataSource(quotesService);
		new QuotesSlidePresenter(mQuotesSlideFragment, Injection.provideQuotesRepository(quotesLocalDataSource, quotesRemoteDataSource), mLanguagePreferences);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_quotes_slide, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deletePrivateFiles();
		mLanguagePreferences.unregisterListeners();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mAlarmSet) {
			// Save alarm status only when it's set
			outState.putBoolean(ALARM_SET, mAlarmSet);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_share:
				loadScreenshot();
				return true;
			case R.id.action_settings:
				showSettingsDialog();
				return true;
			case R.id.action_shuffle:
				// TODO improve
				if (mQuotesSlideFragment != null) {
					mQuotesSlideFragment.shuffle();
				}
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScreenshotLoaderTaskComplete(File file) {
		if (file == null) {
			// TODO show error
			return;
		}

		shareScreenshot(file);

		loader.setVisibility(View.GONE);
	}

	@Override
	public void onScreenshotLoaderTaskInProgress() {
		loader.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		boolean newValue = sharedPreferences.getBoolean(PREF_NOTIFICATION_ENABLED, true);
		if (newValue && !mAlarmSet) {
			// Notifications were enabled and alarm is not set
			setAlarm();
		} else if (!newValue && mAlarmSet) {
			// Notifications were disabled and alarm is set
			cancelAlarm();
		}
	}

	private void loadToolbar() {
		setSupportActionBar(mToolbar);
		// Hide app name and show logo instead
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mToolbar.setLogo(R.drawable.ic_launcher);
	}

	private void loadPreferences() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPreferences.registerOnSharedPreferenceChangeListener(this);

		mLanguagePreferences = new LanguagePreferences(mPreferences);
		mLanguagePreferences.setLanguagePreferencesListener(this);
	}

	private void loadAlarm(Bundle savedInstanceState) {
		if ((savedInstanceState == null || !savedInstanceState.getBoolean(ALARM_SET))
				&& mPreferences.getBoolean(PREF_NOTIFICATION_ENABLED, true)) {
			// First onCreate or alarm not set but should be
			Log.d(TAG, "Setting the alarm");
			setAlarm();
		} else {
			mAlarmSet = savedInstanceState != null && savedInstanceState.getBoolean(ALARM_SET);
		}
	}

	private void loadScreenshot() {
		Bitmap screenshot = ScreenshotUtils.getScreenshot(mContainer);
		if (screenshot == null) {
			Toast.makeText(this, R.string.quote_share_failed, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		new ScreenshotLoader(this, this).execute(screenshot);
	}

	private void showSettingsDialog() {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(this).title(R.string.action_settings)
				.customView(R.layout.dialog_settings, true)
				.positiveText(R.string.button_ok)
				.negativeText(R.string.button_cancel);

		View view = builder.build().getCustomView();

		final boolean notificationsEnabled = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(PREF_NOTIFICATION_ENABLED, true);

		final Switch enableNotificationsSwitch = view.findViewById(R.id.enable_notifications);
		enableNotificationsSwitch.setChecked(notificationsEnabled);

		final String languageSelected = mLanguagePreferences.getLanguage();
		final Spinner spinner = view.findViewById(R.id.language_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_labels, R.layout.spinner_textview);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// Select current language
		int position = Arrays.asList(getResources().getStringArray(R.array.language_values))
				.indexOf(languageSelected);
		spinner.setSelection(position);

		builder.onPositive(new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				// Check if notifications settings were changed
				boolean currentValue = enableNotificationsSwitch.isChecked();
				if (currentValue != notificationsEnabled) {
					// If value was changed, persist
					PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
							.edit()
							.putBoolean(PREF_NOTIFICATION_ENABLED, currentValue)
							.apply();
				}

				// Check if language was changed
				String currentLang = getResources().getStringArray(R.array.language_values)[spinner.getSelectedItemPosition()];
				if (!currentLang.equals(languageSelected)) {
					mLanguagePreferences.setLanguage(currentLang);
				}
			}
		}).show();
	}

	private void shareScreenshot(File file) {
		Uri uri = Uri.fromFile(file); // Convert file path into Uri for sharing
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.quote_share_subject));
		intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.quote_share_text));
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
	}

	private void setAlarm() {
		AlarmReceiver alarm = new AlarmReceiver();
		alarm.setAlarm(this);
		mAlarmSet = true;
	}

	private void cancelAlarm() {
		AlarmReceiver alarm = new AlarmReceiver();
		alarm.cancelAlarm(this);
		mAlarmSet = false;
	}

	private void deletePrivateFiles() {
		File dir;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			dir = Environment.getExternalStorageDirectory();
		} else {
			dir = getCacheDir();
		}
		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("IQ");
			}
		};
		File[] children = dir.listFiles(fileNameFilter);
		for (int i = 0; i < children.length; i++) {
			File file = children[i];
			if (file.isFile()) {
				boolean success = file.delete();
				if (success) {
					Log.d(TAG, "File deleted");
				} else {
					Log.d(TAG, "Couldn't delete file: " + file.getName());
				}
			}
		}
	}

	@Override
	public void onLanguageChange() {
		if (mQuotesSlideFragment != null) {
			mQuotesSlideFragment.onLanguageChange();
		}
	}
}
