package com.sofps.inspirationalquotes.data

import java.io.Serializable

class Quote : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    var id: Long = 0
    var text: String? = null
    var author: String? = null
    var timesShowed: Int = 0
    var language: String? = null

    init {
        id = -1
        timesShowed = 0
    }

}
