package com.sofps.inspirationalquotes.asynctask

import android.os.AsyncTask
import com.sofps.inspirationalquotes.data.DataBaseHelper
import com.sofps.inspirationalquotes.data.Quote
import java.util.ArrayList

class QuotesLoader(
        private val dataBaseHelper: DataBaseHelper,
        private val taskListener: QuotesLoaderTaskListener
) : AsyncTask<String, Void, List<Quote>>() {

    interface QuotesLoaderTaskListener {
        fun onQuotesLoaderTaskComplete(quoteList: List<Quote>)

        fun onQuotesLoaderTaskInProgress()
    }

    override fun doInBackground(vararg strings: String): List<Quote> {
        val language = strings[0]
        return getQuotes(language)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        taskListener.onQuotesLoaderTaskInProgress()
    }

    override fun onPostExecute(quotes: List<Quote>) {
        super.onPostExecute(quotes)
        taskListener.onQuotesLoaderTaskComplete(quotes)
    }

    private fun getQuotes(language: String): List<Quote> {
        val cursor = dataBaseHelper.queryQuotes(language)
        val quotes = ArrayList<Quote>()
        while (cursor.moveToNext()) {
            quotes.add(cursor.quote)
        }
        cursor.close()
        dataBaseHelper.close()
        return quotes
    }
}
