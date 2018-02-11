package com.sofps.inspirationalquotes.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.sofps.inspirationalquotes.data.Quote;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Quote> mQuotes;
    private final List<Integer> mBackgrounds;
    private final List<String> mFonts;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Integer> backgrounds, List<String> fonts) {
        super(fm);
        mBackgrounds = backgrounds;
        mFonts = fonts;
        mQuotes = new ArrayList<>();
    }

    public void setQuotes(List<Quote> quotes) {
        mQuotes.clear();
        mQuotes.addAll(quotes);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        int backgroundPosition = position % mBackgrounds.size();
        int background = mBackgrounds.get(backgroundPosition);

        int fontPosition = position % mFonts.size();
        String font = "font/" + mFonts.get(fontPosition);

        fontPosition = position % mQuotes.size();
        Quote quote = mQuotes.get(fontPosition);

        // TODO to be implemented mDataBaseHelper.addOneTimeShowed(quote);

        return QuotesSlidePageFragment.create(position, background, font, quote.getText(), quote.getAuthor());
    }

    @Override
    public int getCount() {
        return mQuotes.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void shuffle() {
        Collections.shuffle(mBackgrounds);
        Collections.shuffle(mFonts);
        Collections.shuffle(mQuotes);
        notifyDataSetChanged();
    }
}
