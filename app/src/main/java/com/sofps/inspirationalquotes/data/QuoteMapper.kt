package com.sofps.inspirationalquotes.data

import com.sofps.inspirationalquotes.model.QuoteModel

fun QuoteDb.toQuoteModel() = QuoteModel(
        id = id,
        text = text,
        author = author,
        timesShowed = timesShowed,
        language = language
)

fun QuoteModel.toQuoteDb() = QuoteDb(
        id = id,
        text = text,
        author = author,
        timesShowed = timesShowed,
        language = language
)