package com.sofps.inspirationalquotes.model

data class QuoteModel(
        val id: Long,
        val text: String?,
        val author: String?,
        val timesShowed: Long,
        val language: String?
)