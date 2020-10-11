package com.sofps.inspirationalquotes.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sofps.inspirationalquotes.model.QuoteModel

class ScreenSlidePagerAdapter(
        fm: FragmentManager,
        private val backgrounds: MutableList<Int>,
        private val fonts: MutableList<String>,
        private val quoteListener: QuoteListener
) : FragmentStatePagerAdapter(fm) {

    private val quotes: MutableList<QuoteModel> = mutableListOf()

    interface QuoteListener {
        fun onQuoteShow(quote: QuoteModel)
    }

    fun setQuotes(quotes: List<QuoteModel>) {
        this.quotes.clear()
        this.quotes.addAll(quotes)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        val backgroundPosition = position % backgrounds.size
        val background = backgrounds[backgroundPosition]

        val fontPosition = position % fonts.size
        val font = "font/" + fonts[fontPosition]

        val quotePosition = position % quotes.size
        val quote = quotes[quotePosition]

        quoteListener.onQuoteShow(quote)

        return QuotesSlidePageFragment.create(position, background, font, quote.text!!, quote.author!!)
    }

    override fun getCount(): Int {
        return quotes.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun shuffle() {
        backgrounds.shuffle()
        fonts.shuffle()
        quotes.shuffle()
        notifyDataSetChanged()
    }
}
