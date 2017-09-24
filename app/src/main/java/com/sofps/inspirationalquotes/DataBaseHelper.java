package com.sofps.inspirationalquotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {
	private static final String TAG = "DataBaseHelper";

	private static final String DB_NAME = "inspirationalquotes.sqlite";
	private static final int DB_VERSION = 1;

	private static final String TABLE_QUOTE = "quote";
	private static final String COLUMN_QUOTE_ID = "_id";
	private static final String COLUMN_QUOTE_TEXT = "text";
	private static final String COLUMN_QUOTE_AUTHOR = "author";
	private static final String COLUMN_QUOTE_TIMES_SHOWED = "times_showed";
	private static final String COLUMN_QUOTE_LANGUAGE = "language";

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public QuoteCursor queryQuotes(String language) {
		Cursor wrapped = getReadableDatabase().query(TABLE_QUOTE, null,
				COLUMN_QUOTE_LANGUAGE + " = ?", new String[] { language },
				null, null, COLUMN_QUOTE_TIMES_SHOWED + " ASC");
		return new QuoteCursor(wrapped);
	}

	public long addOneTimeShowed(Quote quote) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_QUOTE_TIMES_SHOWED, quote.getTimesShowed() + 1);
		String[] whereArgs = { String.valueOf(quote.getId()) };
		return getWritableDatabase().update(TABLE_QUOTE, cv, "_id = ?",
				whereArgs);
	}

	public static class QuoteCursor extends CursorWrapper {

		public QuoteCursor(Cursor cursor) {
			super(cursor);
		}

		/**
		 * Returns a Quote object configured for the current row, or null if the
		 * current row is invalid.
		 */
		public Quote getQuote() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}
			Quote quote = new Quote();
			long quoteId = getLong(getColumnIndex(COLUMN_QUOTE_ID));
			quote.setId(quoteId);
			String text = getString(getColumnIndex(COLUMN_QUOTE_TEXT));
			quote.setText(text);
			String author = getString(getColumnIndex(COLUMN_QUOTE_AUTHOR));
			quote.setAuthor(author);
			int timesShowed = getInt(getColumnIndex(COLUMN_QUOTE_TIMES_SHOWED));
			quote.setTimesShowed(timesShowed);
			String language = getString(getColumnIndex(COLUMN_QUOTE_LANGUAGE));
			quote.setLanguage(language);
			return quote;
		}
	}

}
