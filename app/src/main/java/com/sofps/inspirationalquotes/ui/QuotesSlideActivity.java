package com.sofps.inspirationalquotes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sofps.inspirationalquotes.AlarmReceiver;
import com.sofps.inspirationalquotes.R;
import com.sofps.inspirationalquotes.asynctask.QuotesLoader;
import com.sofps.inspirationalquotes.asynctask.ScreenshotLoader;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.util.ScreenshotUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuotesSlideActivity extends AppCompatActivity
		implements QuotesLoader.QuotesLoaderTaskListener,
		ScreenshotLoader.ScreenshotLoaderTaskListener,
		SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.progress_spinner) ProgressBar mProgressBar;
    @BindView(R.id.pager) ViewPager mViewPager;

	public static final String PREF_NOTIFICATION_ENABLED = "notificationEnabled";
	public static final String PREF_LANGUAGE = "language";

	private static final String TAG = "QuotesSlideActivity";

	private static final String ALARM_SET = "alarm_set";
	private static final String FONTS = "fonts";
	private static final String QUOTES = "quotes";
	private static final String BACKGROUNDS = "backgrounds";
	private static final String LANGUAGE = "language";
	private static final String[] SUPPORTED_LANGUAGES = { "EN", "ES" };
	private static final String DEFAULT_LANGUAGE = "EN";

	private int mCantPages;
	private int mAvailableBackgrounds[] = { R.drawable.background1,
			R.drawable.background2, R.drawable.background3,
			R.drawable.background4, R.drawable.background5,
			R.drawable.background6, R.drawable.background7,
			R.drawable.background8, R.drawable.background9,
			R.drawable.background10, R.drawable.background11,
			R.drawable.background12, R.drawable.background13,
			R.drawable.background14, R.drawable.background15,
			R.drawable.background16, R.drawable.background17,
			R.drawable.background18, R.drawable.background19,
			R.drawable.background20, R.drawable.background21,
			R.drawable.background22, R.drawable.background23,
			R.drawable.background24, R.drawable.background25,
			R.drawable.background26, R.drawable.background27,
			R.drawable.background28, R.drawable.background29,
			R.drawable.background30, R.drawable.background31,
			R.drawable.background32, R.drawable.background33,
			R.drawable.background34, R.drawable.background35,
			R.drawable.background36 };
	private int mBackgrounds[];
	private ArrayList<String> mFonts = null;
	private ArrayList<Quote> mQuotes = new ArrayList<>();
	private String mCurrentLanguage;

	private PagerAdapter mPagerAdapter;

	private boolean mAlarmSet;

	private SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes_slide);

		ButterKnife.bind(this);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		loadToolbar();
		loadBackgrounds(savedInstanceState);
		loadFonts(savedInstanceState);
		loadCurrentLanguage(savedInstanceState);
		loadQuotes(savedInstanceState);
		loadAlarm(savedInstanceState);

		listenForSharedPreferencesChanges();

		// Workaround to prevent crash android.os.FileUriExposedException: file:///storage/emulated/0/Android/data ...
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
	}

	private void loadToolbar() {
		setSupportActionBar(mToolbar);
		// Hide app name and show logo instead
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mToolbar.setLogo(R.drawable.ic_launcher);
	}

	private void listenForSharedPreferencesChanges() {
		mPreferences.registerOnSharedPreferenceChangeListener(this);
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

	private void loadQuotes(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			loadQuotesForCurrentLanguage();
		} else {
			mQuotes.addAll((ArrayList<Quote>) savedInstanceState.getSerializable(QUOTES));
		}
	}

	private void loadCurrentLanguage(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			mCurrentLanguage = mPreferences.getString(PREF_LANGUAGE, null);
			if (mCurrentLanguage == null) {
				// The language preference is not set, set it
				mCurrentLanguage = Locale.getDefault().getLanguage().toUpperCase();
				if (!Arrays.asList(SUPPORTED_LANGUAGES).contains(mCurrentLanguage)) {
					mCurrentLanguage = DEFAULT_LANGUAGE;
				}
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
						.edit()
						.putString(PREF_LANGUAGE, mCurrentLanguage)
						.apply();
			}
		} else {
			mCurrentLanguage = savedInstanceState.getString(LANGUAGE);
		}
	}

	private void loadFonts(Bundle savedInstanceState) {
		// Load fonts
		try {
			if (savedInstanceState == null) {
				Log.d(TAG, "Loading and shuffling fonts from assets");
				String[] fonts = getAssets().list("font");
				mFonts = new ArrayList<>(Arrays.asList(fonts));
				Collections.shuffle(mFonts);
			} else {
				Log.d(TAG, "Loading fonts from bundle");
				mFonts = savedInstanceState.getStringArrayList(FONTS);
			}
		} catch (IOException e) {
			throw new Error("Unable to open fonts");
		}
	}

	private void loadBackgrounds(Bundle savedInstanceState) {
		// Load backgrounds
		if (savedInstanceState == null) {
			Log.d(TAG, "Loading and shuffling backgrounds");
			mBackgrounds = mAvailableBackgrounds;
			shuffleArray(mBackgrounds);
		} else {
			Log.d(TAG, "Loading backgrounds from bundle");
			mBackgrounds = savedInstanceState.getIntArray(BACKGROUNDS);
		}
	}

	private void loadQuotesForCurrentLanguage() {
		new QuotesLoader(this, this).execute(mCurrentLanguage);
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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntArray(BACKGROUNDS, mBackgrounds);
		outState.putStringArrayList(FONTS, mFonts);
		outState.putSerializable(QUOTES, mQuotes);
		if (mAlarmSet) {
			// Save alarm status only when it's set
			outState.putBoolean(ALARM_SET, mAlarmSet);
		}
		outState.putString(LANGUAGE, mCurrentLanguage);
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
				shuffleEverything();
				mPagerAdapter.notifyDataSetChanged();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void loadScreenshot() {
		Bitmap screenshot = ScreenshotUtils.getScreenshot(mViewPager);
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

		final String languageSelected = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(PREF_LANGUAGE, null);
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
					PreferenceManager.getDefaultSharedPreferences(QuotesSlideActivity.this)
							.edit()
							.putBoolean(PREF_NOTIFICATION_ENABLED, currentValue)
							.apply();
				}

				// Check if language was changed
				String currentLang = getResources().getStringArray(R.array.language_values)[spinner.getSelectedItemPosition()];
				if (!currentLang.equals(languageSelected)) {
					PreferenceManager.getDefaultSharedPreferences(QuotesSlideActivity.this)
							.edit()
							.putString(PREF_LANGUAGE, currentLang)
							.apply();
				}
			}
		}).show();
	}

	@Override
	public void onQuotesLoaderTaskComplete(List<Quote> quoteList) {
		mQuotes.clear();
		mQuotes.addAll(quoteList);
		mCantPages = mQuotes.size();

		if (mPagerAdapter == null) {
			mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		} else {
			mPagerAdapter.notifyDataSetChanged();
		}

		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onQuotesLoaderTaskInProgress() {
		setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onScreenshotLoaderTaskComplete(File file) {
		if (file == null) {
			// TODO show error
			return;
		}

		shareScreenshot(file);

		mProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onScreenshotLoaderTaskInProgress() {
		mProgressBar.setVisibility(View.VISIBLE);
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

		String language = sharedPreferences.getString(PREF_LANGUAGE, null);
		if (!mCurrentLanguage.equals(language)) {
			mCurrentLanguage = language;
			loadQuotesForCurrentLanguage();
		}
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			int pos = position % mBackgrounds.length;
			int background = mBackgrounds[pos];

			pos = position % mFonts.size();
			String font = "font/" + mFonts.get(pos);

			pos = position % mQuotes.size();
			Quote quote = mQuotes.get(pos);

			// TODO to be implemented mDataBaseHelper.addOneTimeShowed(quote);

			return QuotesSlidePageFragment.create(position, background, font,
					quote.getText(), quote.getAuthor());
		}

		@Override
		public int getCount() {
			return mCantPages;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

	private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
				float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horizontalMargin - verticalMargin / 2);
				} else {
					view.setTranslationX(-horizontalMargin + verticalMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	private void shuffleArray(int[] array) {
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			if (index != i) {
				array[index] ^= array[i];
				array[i] ^= array[index];
				array[index] ^= array[i];
			}
		}
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

	private void shuffleEverything() {
		shuffleArray(mBackgrounds);
		Collections.shuffle(mFonts);
		Collections.shuffle(mQuotes);
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

}
