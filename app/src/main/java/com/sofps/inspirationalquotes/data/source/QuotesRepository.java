package com.sofps.inspirationalquotes.data.source;

import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.asynctask.QuotesLoader;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.Quote;

public class QuotesRepository {

    private final DataBaseHelper mDataBaseHelper;

    public QuotesRepository(@NonNull DataBaseHelper dataBaseHelper) {
        mDataBaseHelper = dataBaseHelper;
    }

    public void loadQuotesForLanguage(String language, QuotesLoader.QuotesLoaderTaskListener listener) {
        new QuotesLoader(mDataBaseHelper, listener).execute(language);
    }

    public void addOneTimeShowed(Quote quote) {
        mDataBaseHelper.addOneTimeShowed(quote);
    }
}
