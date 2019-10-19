package com.sofps.inspirationalquotes.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

/**
 * Constructor Takes and keeps a reference of the passed context in order to
 * access to the application assets and resources.
 *
 * @param context
 */
class DataBaseHelper(context: Context) : SQLiteAssetHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val TAG = "DataBaseHelper"

        private const val DB_NAME = "inspirationalquotes.sqlite"
        private const val DB_VERSION = 1

        private const val TABLE_QUOTE = "quote"
        private const val COLUMN_QUOTE_ID = "_id"
        private const val COLUMN_QUOTE_TEXT = "text"
        private const val COLUMN_QUOTE_AUTHOR = "author"
        private const val COLUMN_QUOTE_TIMES_SHOWED = "times_showed"
        private const val COLUMN_QUOTE_LANGUAGE = "language"
    }

    fun getQuote(language: String): Quote? {
        return queryQuotes(language).quote
    }

    fun queryQuotes(language: String): QuoteCursor {
        val wrapped = readableDatabase.query(
                TABLE_QUOTE,
                null,
                "$COLUMN_QUOTE_LANGUAGE = ?",
                arrayOf(language),
                null,
                null,
                "$COLUMN_QUOTE_TIMES_SHOWED ASC")
        return QuoteCursor(wrapped)
    }

    fun addOneTimeShowed(quote: Quote): Long {
        val cv = ContentValues().apply {
            put(COLUMN_QUOTE_TIMES_SHOWED, quote.timesShowed + 1)
        }
        val whereArgs = arrayOf("${quote.id}")
        return writableDatabase.update(TABLE_QUOTE, cv, "_id = ?",
                whereArgs).toLong()
    }

    fun insertQuote(quote: Quote) {
        writableDatabase.insert(TABLE_QUOTE, null, toContentValues(quote))
    }

    class QuoteCursor(cursor: Cursor) : CursorWrapper(cursor) {

        /**
         * Returns a Quote object configured for the current row, or null if the
         * current row is invalid.
         */
        val quote: Quote?
            get() {
                if (isBeforeFirst || isAfterLast) {
                    return null
                }
                return Quote().apply {
                    id = getLong(getColumnIndex(COLUMN_QUOTE_ID))
                    text = getString(getColumnIndex(COLUMN_QUOTE_TEXT))
                    author = getString(getColumnIndex(COLUMN_QUOTE_AUTHOR))
                    timesShowed = getInt(getColumnIndex(COLUMN_QUOTE_TIMES_SHOWED))
                    language = getString(getColumnIndex(COLUMN_QUOTE_LANGUAGE))
                }
            }
    }

    private fun toContentValues(quote: Quote) =
            ContentValues().apply {
                // TODO contentValues.put(COLUMN_QUOTE_ID, );
                put(COLUMN_QUOTE_TEXT, quote.text)
                put(COLUMN_QUOTE_AUTHOR, quote.author)
                put(COLUMN_QUOTE_LANGUAGE, quote.language)
                put(COLUMN_QUOTE_TIMES_SHOWED, quote.timesShowed)
            }

}
