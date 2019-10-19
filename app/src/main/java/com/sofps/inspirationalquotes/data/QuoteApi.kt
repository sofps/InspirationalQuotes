package com.sofps.inspirationalquotes.data

import com.google.gson.annotations.SerializedName

// TODO this data model is incorrect and it's not parsed correctly when trying to get the quote of the day from the api
class QuoteApi {

    @SerializedName("author")
    var author: String? = null
        private set
    @SerializedName("background")
    var background: String? = null
        private set
    @SerializedName("category")
    var category: String? = null
        private set
    @SerializedName("date")
    var date: String? = null
        private set
    @SerializedName("id")
    var id: String? = null
        private set
    @SerializedName("length")
    var length: Any? = null
        private set
    @SerializedName("permalink")
    var permalink: String? = null
        private set
    @SerializedName("quote")
    var quote: String? = null
        private set
    @SerializedName("tags")
    var tags: List<String>? = null
        private set
    @SerializedName("title")
    var title: String? = null
        private set

    val isValid: Boolean
        get() = author != null && author!!.isNotEmpty() && quote != null && quote!!.isNotEmpty()

    class Builder {

        private var author: String? = null
        private var background: String? = null
        private var category: String? = null
        private var date: String? = null
        private var id: String? = null
        private var length: Any? = null
        private var permalink: String? = null
        private var quote: String? = null
        private var tags: List<String>? = null
        private var title: String? = null

        fun withAuthor(author: String): Builder {
            this.author = author
            return this
        }

        fun withBackground(background: String): Builder {
            this.background = background
            return this
        }

        fun withCategory(category: String): Builder {
            this.category = category
            return this
        }

        fun withDate(date: String): Builder {
            this.date = date
            return this
        }

        fun withId(id: String): Builder {
            this.id = id
            return this
        }

        fun withLength(length: Any): Builder {
            this.length = length
            return this
        }

        fun withPermalink(permalink: String): Builder {
            this.permalink = permalink
            return this
        }

        fun withQuote(quote: String): Builder {
            this.quote = quote
            return this
        }

        fun withTags(tags: List<String>): Builder {
            this.tags = tags
            return this
        }

        fun withTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun build() =
                QuoteApi().apply {
                    this.author = author
                    this.background = background
                    this.category = category
                    this.date = date
                    this.id = id
                    this.length = length
                    this.permalink = permalink
                    this.quote = quote
                    this.tags = tags
                    this.title = title
                }
    }
}
