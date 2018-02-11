package com.sofps.inspirationalquotes.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// TODO this data model is incorrect and it's not parsed correctly when trying to get the quote of the day from the api
public class QuoteApi {

    @SerializedName("author") private String mAuthor;
    @SerializedName("background") private String mBackground;
    @SerializedName("category") private String mCategory;
    @SerializedName("date") private String mDate;
    @SerializedName("id") private String mId;
    @SerializedName("length") private Object mLength;
    @SerializedName("permalink") private String mPermalink;
    @SerializedName("quote") private String mQuote;
    @SerializedName("tags") private List<String> mTags;
    @SerializedName("title") private String mTitle;

    public String getAuthor() {
        return mAuthor;
    }

    public String getBackground() {
        return mBackground;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getDate() {
        return mDate;
    }

    public String getId() {
        return mId;
    }

    public Object getLength() {
        return mLength;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public String getQuote() {
        return mQuote;
    }

    public List<String> getTags() {
        return mTags;
    }

    public String getTitle() {
        return mTitle;
    }

    public static class Builder {

        private String mAuthor;
        private String mBackground;
        private String mCategory;
        private String mDate;
        private String mId;
        private Object mLength;
        private String mPermalink;
        private String mQuote;
        private List<String> mTags;
        private String mTitle;

        public QuoteApi.Builder withAuthor(String author) {
            mAuthor = author;
            return this;
        }

        public QuoteApi.Builder withBackground(String background) {
            mBackground = background;
            return this;
        }

        public QuoteApi.Builder withCategory(String category) {
            mCategory = category;
            return this;
        }

        public QuoteApi.Builder withDate(String date) {
            mDate = date;
            return this;
        }

        public QuoteApi.Builder withId(String id) {
            mId = id;
            return this;
        }

        public QuoteApi.Builder withLength(Object length) {
            mLength = length;
            return this;
        }

        public QuoteApi.Builder withPermalink(String permalink) {
            mPermalink = permalink;
            return this;
        }

        public QuoteApi.Builder withQuote(String quote) {
            mQuote = quote;
            return this;
        }

        public QuoteApi.Builder withTags(List<String> tags) {
            mTags = tags;
            return this;
        }

        public QuoteApi.Builder withTitle(String title) {
            mTitle = title;
            return this;
        }

        public QuoteApi build() {
            QuoteApi QuoteDao = new QuoteApi();
            QuoteDao.mAuthor = mAuthor;
            QuoteDao.mBackground = mBackground;
            QuoteDao.mCategory = mCategory;
            QuoteDao.mDate = mDate;
            QuoteDao.mId = mId;
            QuoteDao.mLength = mLength;
            QuoteDao.mPermalink = mPermalink;
            QuoteDao.mQuote = mQuote;
            QuoteDao.mTags = mTags;
            QuoteDao.mTitle = mTitle;
            return QuoteDao;
        }
    }
}
