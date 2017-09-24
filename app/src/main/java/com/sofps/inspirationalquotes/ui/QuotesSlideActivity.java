package com.sofps.inspirationalquotes.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import com.sofps.inspirationalquotes.AlarmReceiver;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.DataBaseHelper.QuoteCursor;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.R;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class QuotesSlideActivity extends FragmentActivity {
	private static final String TAG = "QuotesSlideActivity";

	private static final String ALARM_SET = "alarm_set";
	private static final String FONTS = "fonts";
	private static final String QUOTES = "quotes";
	private static final String BACKGROUNDS = "backgrounds";
	private static final String LANGUAGE = "language";
	private static final String[] SUPPORTED_LANGUAGES = { "EN", "ES" };
	private static final String DEFAULT_LANGUAGE = "EN";
	private static final String IS_ICON_CREATED = "icon";

	private static boolean startedFlag;

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
	private ArrayList<Quote> mQuotes = null;
	private String mCurrentLanguage;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	private DataBaseHelper mDabaBaseHelper;

	private boolean mAlarmSet;

	private SharedPreferences mPreferences;
	private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		// Request for the progress bar to be shown in the title
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_screen_slide);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Remove app title from action bar
			getActionBar().setDisplayShowTitleEnabled(false);
		}

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		Loader loader = new Loader();
		loader.execute(savedInstanceState);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_screen_slide, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		if (isFinishing()) {
			startedFlag = false;
		}
		deletePrivateFiles();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "Saving backgrounds");
		outState.putIntArray(BACKGROUNDS, mBackgrounds);
		Log.d(TAG, "Saving fonts");
		outState.putStringArrayList(FONTS, mFonts);
		Log.d(TAG, "Saving quotes");
		outState.putSerializable(QUOTES, mQuotes);
		if (mAlarmSet) {
			// Save alarm status only when it's set
			Log.d(TAG, "Saving alarm status");
			outState.putBoolean(ALARM_SET, mAlarmSet);
		}
		Log.d(TAG, "Saving language");
		outState.putString(LANGUAGE, mCurrentLanguage);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.action_settings:
			DialogFragment dialog = new SettingsDialogFragment();
			dialog.show(getSupportFragmentManager(), "SettingsDialogFragment");
			return true;
		case R.id.action_shuffle:
			shuffleEverything();
			mPagerAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

			mDabaBaseHelper.addOneTimeShowed(quote);

			Log.d(TAG, "getItem " + position + ": " + "background: "
					+ background + ", font: " + font);

			return QuotesSlidePageFragment.create(position, background, font,
					quote.getText(), quote.getAuthor());
		}

		@Override
		public int getCount() {
			return mCantPages;
		}

		@Override
		public int getItemPosition(Object object) {
			Log.d(TAG, "getItemPosition");
			return POSITION_NONE;
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
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

	private class Loader extends AsyncTask<Bundle, Void, Void> {

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Bundle... params) {
			Log.d(TAG, "doInBackground");

			mPagerAdapter = new ScreenSlidePagerAdapter(
					getSupportFragmentManager());

			Bundle savedInstanceState = params[0];
			// Load backgrounds
			if (savedInstanceState == null) {
				Log.d(TAG, "Loading and shuffling backgrounds");
				mBackgrounds = mAvailableBackgrounds;
				shuffleArray(mBackgrounds);
			} else {
				Log.d(TAG, "Loading backgrounds from bundle");
				mBackgrounds = savedInstanceState.getIntArray(BACKGROUNDS);
			}

			// Load fonts
			try {
				if (savedInstanceState == null) {
					Log.d(TAG, "Loading and shuffling fonts from assets");
					String[] fonts = getAssets().list("font");
					mFonts = new ArrayList<String>(Arrays.asList(fonts));
					Collections.shuffle(mFonts);
				} else {
					Log.d(TAG, "Loading fonts from bundle");
					mFonts = savedInstanceState.getStringArrayList(FONTS);
				}
			} catch (IOException e) {
				throw new Error("Unable to open fonts");
			}

			mDabaBaseHelper = new DataBaseHelper(QuotesSlideActivity.this);
			if (savedInstanceState == null) {
				mCurrentLanguage = mPreferences.getString(
						SettingsDialogFragment.PREF_LANGUAGE, null);
				if (mCurrentLanguage == null) {
					// The language preference is not set, set it
					mCurrentLanguage = Locale.getDefault().getLanguage()
							.toUpperCase();
					if (!Arrays.asList(SUPPORTED_LANGUAGES).contains(
							mCurrentLanguage)) {
						mCurrentLanguage = DEFAULT_LANGUAGE;
					}
					PreferenceManager
							.getDefaultSharedPreferences(
									getApplicationContext())
							.edit()
							.putString(SettingsDialogFragment.PREF_LANGUAGE,
									mCurrentLanguage).commit();
				}
				Log.d(TAG, "The selected language is " + mCurrentLanguage);
				loadQuotes();
			} else {
				mCurrentLanguage = savedInstanceState.getString(LANGUAGE);
				mQuotes = (ArrayList<Quote>) savedInstanceState
						.getSerializable(QUOTES);
			}
			mCantPages = mQuotes.size();

			mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
				public void onSharedPreferenceChanged(SharedPreferences prefs,
						String key) {
					Log.d(TAG, "Shared Preferences were changed");
					boolean newValue = prefs.getBoolean(
							SettingsDialogFragment.PREF_NOTIFICATION_ENABLED,
							true);
					if (newValue && !mAlarmSet) {
						// Notifications were enabled and alarm is not set
						setAlarm();
					} else if (!newValue && mAlarmSet) {
						// Notifications were disabled and alarm is set
						cancelAlarm();
					}

					String language = prefs.getString(
							SettingsDialogFragment.PREF_LANGUAGE, null);
					if (mCurrentLanguage != language) {
						Log.d(TAG, "Language was modified");
						mCurrentLanguage = language;
						loadQuotes();
						mPagerAdapter.notifyDataSetChanged();
					}
				}
			};
			mPreferences
					.registerOnSharedPreferenceChangeListener(mPrefListener);

			if ((savedInstanceState == null || !savedInstanceState
					.getBoolean(ALARM_SET))
					&& mPreferences.getBoolean(
							SettingsDialogFragment.PREF_NOTIFICATION_ENABLED,
							true)) {
				// First onCreate or alarm not set but should be
				Log.d(TAG, "Setting the alarm");
				setAlarm();
			} else {
				mAlarmSet = savedInstanceState == null ? false
						: savedInstanceState.getBoolean(ALARM_SET);
			}

			if (!mPreferences.getBoolean(IS_ICON_CREATED, false)) {
				addShortcut();
				mPreferences.edit().putBoolean(IS_ICON_CREATED, true).commit();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "onPostExecute");

			super.onPostExecute(result);

			// Instantiate a ViewPager and a PagerAdapter.
			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mPagerAdapter);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mPager.setPageTransformer(true, new ZoomOutPageTransformer());
			}

			setProgressBarIndeterminateVisibility(false);

			if (!startedFlag) {
				AppRater.app_launched(QuotesSlideActivity.this);
				startedFlag = true;
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
		Log.d(TAG, "setAlarm");
		AlarmReceiver alarm = new AlarmReceiver();
		alarm.setAlarm(this);
		mAlarmSet = true;
	}

	private void cancelAlarm() {
		Log.d(TAG, "cancelAlarm");
		AlarmReceiver alarm = new AlarmReceiver();
		alarm.cancelAlarm(this);
		mAlarmSet = false;
	}

	private void shuffleEverything() {
		shuffleArray(mBackgrounds);
		Collections.shuffle(mFonts);
		Collections.shuffle(mQuotes);
	}

	private void loadQuotes() {
		QuoteCursor cursor = mDabaBaseHelper.queryQuotes(mCurrentLanguage);
		mQuotes = new ArrayList<Quote>();
		while (cursor.moveToNext()) {
			mQuotes.add(cursor.getQuote());
		}
		cursor.close();
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

	private void addShortcut() {
		Intent shortcutIntent = new Intent(getApplicationContext(),
				QuotesSlideActivity.class);

		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.ic_launcher));

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);
	}

}
