package com.sofps.inspirationalquotes.data.source.local;

import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.data.source.QuotesDataSource;
import java.util.ArrayList;
import java.util.List;

public class QuotesLocalDataSource implements QuotesDataSource {

    private final DataBaseHelper mDataBaseHelper;

    public QuotesLocalDataSource(DataBaseHelper dataBaseHelper) {
        mDataBaseHelper = dataBaseHelper;
    }

    @Override
    public void getQuote(@NonNull String language, @NonNull GetQuoteCallback callback) {
        callback.onQuoteLoaded(getQuotes(language).get(0)); // TODO
    }

    public void persist(Quote quote) {
        mDataBaseHelper.insertQuote(quote);
    }

    public void addOneTimeShowed(Quote quote) {
        mDataBaseHelper.addOneTimeShowed(quote);
    }

    private List<Quote> getQuotes(String language) {
        DataBaseHelper.QuoteCursor cursor = mDataBaseHelper.queryQuotes(language);
        List<Quote> quotes = new ArrayList<>();
        while (cursor.moveToNext()) {
            quotes.add(cursor.getQuote());
        }
        cursor.close();
        mDataBaseHelper.close();
        return quotes;
    }
}
