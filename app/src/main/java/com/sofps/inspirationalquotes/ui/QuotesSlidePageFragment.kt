package com.sofps.inspirationalquotes.ui

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sofps.inspirationalquotes.R
import kotlinx.android.synthetic.main.fragment_quotes_slide_page.*

class QuotesSlidePageFragment : Fragment(), QuotesSlidePageContract.View {

    companion object {

        const val ARG_PAGE = "page"
        const val ARG_BACKGROUND = "background"
        const val ARG_FONT = "font"
        const val ARG_QUOTE = "quote"
        const val ARG_AUTHOR = "author"

        private const val AUTHOR_PREFIX = "-"

        fun create(pageNumber: Int,
                   background: Int, font: String, quote: String, author: String) =
                QuotesSlidePageFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PAGE, pageNumber)
                        putInt(ARG_BACKGROUND, background)
                        putString(ARG_FONT, font)
                        putString(ARG_QUOTE, quote)
                        putString(ARG_AUTHOR, author)
                    }
                }
    }

    private var rootView: ViewGroup? = null

    private var pageNumber: Int = 0
    private var background: Int = 0
    private var font: String? = null
    private var quote: String? = null
    private var author: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true

        pageNumber = arguments!!.getInt(ARG_PAGE)
        background = arguments!!.getInt(ARG_BACKGROUND)
        font = arguments!!.getString(ARG_FONT)
        quote = arguments!!.getString(ARG_QUOTE)
        author = arguments!!.getString(ARG_AUTHOR)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(
                R.layout.fragment_quotes_slide_page, container, false) as ViewGroup
        rootView?.setBackgroundResource(background)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val font = Typeface.createFromAsset(activity!!.assets, font)

        quote_text_view.typeface = font
        quote_text_view.text = quote

        author_text_view.typeface = font
        author_text_view.text = "$AUTHOR_PREFIX $author"

        val size: Float
        if (this.font!!.contains("large") && quote!!.length >= 50) {
            size = resources.getDimension(R.dimen.long_quote_size) / resources.displayMetrics.density
            quote_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            author_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
        } else if (this.font!!.contains("small")) {
            size = resources.getDimension(
                    R.dimen.quote_size_for_smaller_fonts) / resources.displayMetrics.density
            quote_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
            author_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
        }
    }

    override fun setPresenter(presenter: QuotesSlidePageContract.Presenter) {

    }
}
