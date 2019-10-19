package com.sofps.inspirationalquotes.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.sofps.inspirationalquotes.data.Quote;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public interface QuoteListener {
        void onQuoteShow(Quote quote);
    }

    private final List<Quote> mQuotes;
    private final List<Integer> mBackgrounds;
    private final List<String> mFonts;
    private final QuoteListener mQuoteListener;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Integer> backgrounds, List<String> fonts, QuoteListener quoteListener) {
        super(fm);
        mBackgrounds = backgrounds;
        mFonts = fonts;
        mQuotes = new ArrayList<>();
        mQuoteListener = quoteListener;
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

        int quotePosition = position % mQuotes.size();
        Quote quote = mQuotes.get(quotePosition);

        mQuoteListener.onQuoteShow(quote);

        return QuotesSlidePageFragment.Companion.create(position, background, font, quote.getText(), quote.getAuthor());
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
