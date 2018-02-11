package com.sofps.inspirationalquotes.data;

import android.content.Context;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

public class QuotesLoader extends AsyncTask<String, Void, List<Quote>> {

    public interface TaskListener {
        void onTaskComplete(List<Quote> quoteList);

        void onTaskInProgress();
    }

    private final DataBaseHelper mDataBaseHelper;
    private final TaskListener mTaskListener;

    public QuotesLoader(Context context, TaskListener listener) {
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
        mTaskListener.onTaskInProgress();
    }

    @Override
    protected void onPostExecute(List<Quote> quotes) {
        super.onPostExecute(quotes);
        mTaskListener.onTaskComplete(quotes);
    }

    private List<Quote> getQuotes(String language) {
        DataBaseHelper.QuoteCursor cursor = mDataBaseHelper.queryQuotes(language);
        List<Quote> quotes = new ArrayList<>();
        while (cursor.moveToNext()) {
            quotes.add(cursor.getQuote());
        }
        cursor.close();
        return quotes;
    }
}
