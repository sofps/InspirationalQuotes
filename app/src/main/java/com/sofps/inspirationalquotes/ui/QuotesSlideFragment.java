package com.sofps.inspirationalquotes.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.sofps.inspirationalquotes.R;
import com.sofps.inspirationalquotes.asynctask.QuotesLoader;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.util.LanguagePreferences;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuotesSlideFragment extends android.support.v4.app.Fragment
        implements QuotesLoader.QuotesLoaderTaskListener {

    @BindView(R.id.pager) ViewPager mViewPager;

    private static final String TAG = "MainActivity";

    private static final String FONTS = "fonts";
    private static final String QUOTES = "quotes";
    private static final String BACKGROUNDS = "backgrounds";

    private int mAvailableBackgrounds[] = {
            R.drawable.background1, R.drawable.background2, R.drawable.background3,
            R.drawable.background4, R.drawable.background5, R.drawable.background6,
            R.drawable.background7, R.drawable.background8, R.drawable.background9,
            R.drawable.background10, R.drawable.background11, R.drawable.background12,
            R.drawable.background13, R.drawable.background14, R.drawable.background15,
            R.drawable.background16, R.drawable.background17, R.drawable.background18,
            R.drawable.background19, R.drawable.background20, R.drawable.background21,
            R.drawable.background22, R.drawable.background23, R.drawable.background24,
            R.drawable.background25, R.drawable.background26, R.drawable.background27,
            R.drawable.background28, R.drawable.background29, R.drawable.background30,
            R.drawable.background31, R.drawable.background32, R.drawable.background33,
            R.drawable.background34, R.drawable.background35, R.drawable.background36
    };
    private ArrayList<Integer> mBackgrounds;
    private ArrayList<String> mFonts = null;
    private ArrayList<Quote> mQuotes = new ArrayList<>();

    private ScreenSlidePagerAdapter mPagerAdapter;

    private Unbinder mUnbinder;
    private LanguagePreferences mLanguagePreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quotes_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        mLanguagePreferences = new LanguagePreferences(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        loadBackgrounds(savedInstanceState);
        loadFonts(savedInstanceState);
        loadQuotes(savedInstanceState);

        // Workaround to prevent crash android.os.FileUriExposedException: file:///storage/emulated/0/Android/data ...
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BACKGROUNDS, mBackgrounds);
        outState.putStringArrayList(FONTS, mFonts);
        outState.putSerializable(QUOTES, mQuotes);
    }

    @Override
    public void onQuotesLoaderTaskComplete(List<Quote> quoteList) {
        mQuotes.clear();
        mQuotes.addAll(quoteList);

        if (mPagerAdapter == null) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager(), mBackgrounds, mFonts);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            mPagerAdapter.setQuotes(mQuotes);
        } else {
            mPagerAdapter.setQuotes(mQuotes);
        }

        // TODO setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onQuotesLoaderTaskInProgress() {
        // TODO setProgressBarIndeterminateVisibility(true);
    }

    private void loadQuotes(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadQuotesForCurrentLanguage();
        } else {
            mQuotes.addAll((ArrayList<Quote>) savedInstanceState.getSerializable(QUOTES));
        }
    }

    private void loadFonts(Bundle savedInstanceState) {
        // Load fonts
        try {
            if (savedInstanceState == null) {
                String[] fonts = getActivity().getAssets().list("font");
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

            mBackgrounds = new ArrayList<>(mAvailableBackgrounds.length);
            for (int i = 0; i < mAvailableBackgrounds.length; i++) {
                mBackgrounds.add(mAvailableBackgrounds[i]);
            }
            Collections.shuffle(mBackgrounds);
        } else {
            Log.d(TAG, "Loading backgrounds from bundle");
            mBackgrounds = savedInstanceState.getIntegerArrayList(BACKGROUNDS);
        }
    }

    public void loadQuotesForCurrentLanguage() {
        new QuotesLoader(getActivity(), this).execute(mLanguagePreferences.getLanguage());
    }

    public void shuffle() {
        mPagerAdapter.shuffle();
    }

    private static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // getActivity() page is way off-screen to the left.
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
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1
                        - MIN_ALPHA));
            } else { // (1,+Infinity]
                // getActivity() page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
