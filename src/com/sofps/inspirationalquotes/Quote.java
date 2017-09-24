package com.sofps.inspirationalquotes;

import java.io.Serializable;

public class Quote implements Serializable {
	private static final long serialVersionUID = 1L;

	private long mId;
	private String mText;
	private String mAuthor;
	private int mTimesShowed;
	private String mLanguage;

	public Quote() {
		mId = -1;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

	public int getTimesShowed() {
		return mTimesShowed;
	}

	public void setTimesShowed(int timesShowed) {
		mTimesShowed = timesShowed;
	}

	public String getLanguage() {
		return mLanguage;
	}

	public void setLanguage(String language) {
		mLanguage = language;
	}

}
