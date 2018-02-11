package com.sofps.inspirationalquotes.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.Quote;
import java.util.ArrayList;
import java.util.List;

public class QuotesLoader extends AsyncTask<String, Void, List<Quote>> {

    public interface QuotesLoaderTaskListener {
        void onQuotesLoaderTaskComplete(List<Quote> quoteList);

        void onQuotesLoaderTaskInProgress();
    }

    private final DataBaseHelper mDataBaseHelper;
    private final QuotesLoaderTaskListener mTaskListener;

    public QuotesLoader(Context context, QuotesLoaderTaskListener listener) {
        mDataBaseHelper = new DataBaseHelper(context);
        mTaskListener = listener;
    }

    @Override
    protected List<Quote> doInBackground(String... strings) {
        String language = strings[0];
        return getQuotes(language);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mTaskListener.onQuotesLoaderTaskInProgress();
    }

    @Override
    protected void onPostExecute(List<Quote> quotes) {
        super.onPostExecute(quotes);
        mTaskListener.onQuotesLoaderTaskComplete(quotes);
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
