package com.sofps.inspirationalquotes.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sofps.inspirationalquotes.R;
import com.sofps.inspirationalquotes.util.LayoutedTextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class ScreenSlidePageFragment extends Fragment {
	private static final String TAG = "ScreenSlidePageFragment";

	public static final String ARG_PAGE = "page";
	public static final String ARG_BACKGROUND = "background";
	public static final String ARG_FONT = "font";
	public static final String ARG_QUOTE = "quote";
	public static final String ARG_AUTHOR = "author";

	private static final String AUTHOR_PREFIX = "-";

	private ViewGroup rootView;

	private int mPageNumber;
	private int mBackground;
	private String mFont;
	private String mQuote;
	private String mAuthor;

	public static ScreenSlidePageFragment create(int pageNumber,
			int background, String font, String quote, String author) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		args.putInt(ARG_BACKGROUND, background);
		args.putString(ARG_FONT, font);
		args.putString(ARG_QUOTE, quote);
		args.putString(ARG_AUTHOR, author);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlidePageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		mPageNumber = getArguments().getInt(ARG_PAGE);
		mBackground = getArguments().getInt(ARG_BACKGROUND);
		mFont = getArguments().getString(ARG_FONT);
		mQuote = getArguments().getString(ARG_QUOTE);
		mAuthor = getArguments().getString(ARG_AUTHOR);

		Log.d(TAG, "onCreate " + mPageNumber);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView " + mPageNumber);
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);
		rootView.setBackgroundResource(mBackground);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				mFont);

		TextView quoteTextView = (TextView) rootView.findViewById(R.id.quote);
		quoteTextView.setTypeface(font);
		quoteTextView.setText(mQuote);

		LayoutedTextView authorTextView = (LayoutedTextView) rootView
				.findViewById(R.id.author);
		authorTextView.setTypeface(font);
		authorTextView.setText(AUTHOR_PREFIX + " " + mAuthor);

		float size;
		if ((mFont.contains("large") && mQuote.length() >= 50)) {
			size = getResources().getDimension(R.dimen.long_quote_size)
					/ getResources().getDisplayMetrics().density;
			quoteTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
			authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		} else if (mFont.contains("small")) {
			size = getResources().getDimension(
					R.dimen.quote_size_for_smaller_fonts)
					/ getResources().getDisplayMetrics().density;
			quoteTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
			authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		}

		return rootView;
	}

	/**
	 * Un poco chancho esto aca pero fue la unica forma que encontre para poner
	 * invisible la publicidad para el screenshot
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.action_share:
			ScreenshotLoader loader = new ScreenshotLoader();
			loader.execute();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public int getPageNumber() {
		return mPageNumber;
	}

	private class ScreenshotLoader extends AsyncTask<Void, Void, File> {

		private Bitmap mScreenshot;

		@Override
		protected File doInBackground(Void... arg0) {
			File dir;
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				dir = Environment.getExternalStorageDirectory();
			} else {
				dir = getActivity().getCacheDir();
			}
			File myPath = new File(dir, "IQ_" + UUID.randomUUID() + ".jpg");
			Log.d(TAG, "Path: " + myPath.getAbsolutePath());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(myPath);
				mScreenshot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				mScreenshot.recycle();
				mScreenshot = null;
				fos.flush();
				fos.close();
				return myPath;
			} catch (FileNotFoundException e) {
				throw new Error("File not found");
			} catch (Exception e) {
				throw new Error(e);
			}
		}

		@Override
		protected void onPreExecute() {
			getActivity().setProgressBarIndeterminateVisibility(true);
			takeScreenShot();
		}

		@SuppressLint("InlinedApi")
		@Override
		protected void onPostExecute(File result) {
				if (result == null) {
					throw new Error("File not found");
				}

				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/*");
				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(result));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				}
				startActivity(Intent.createChooser(share,
						getString(R.string.share)));

				getActivity().setProgressBarIndeterminateVisibility(false);
		}

		private void takeScreenShot() {
			rootView.buildDrawingCache();
			rootView.setDrawingCacheEnabled(true);
			mScreenshot = Bitmap.createBitmap(rootView.getDrawingCache(), 0, 0,
					rootView.getWidth(), rootView.getHeight());
			rootView.destroyDrawingCache();
			rootView.setDrawingCacheEnabled(false);
		}

	}
}
