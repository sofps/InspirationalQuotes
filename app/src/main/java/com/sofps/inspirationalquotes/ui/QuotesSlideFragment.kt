package com.sofps.inspirationalquotes.ui

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.sofps.inspirationalquotes.R
import com.sofps.inspirationalquotes.data.Quote
import kotlinx.android.synthetic.main.fragment_quotes_slide.*
import java.io.IOException
import java.io.Serializable
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class QuotesSlideFragment : Fragment(), QuotesSlideContract.View {

    companion object {

        private const val TAG = "MainActivity"

        private const val FONTS = "fonts"
        private const val QUOTES = "quotes"
        private const val BACKGROUNDS = "backgrounds"
    }

    private val availableBackgrounds = intArrayOf(
            R.drawable.background1,
            R.drawable.background2,
            R.drawable.background3,
            R.drawable.background4,
            R.drawable.background5,
            R.drawable.background6,
            R.drawable.background7,
            R.drawable.background8,
            R.drawable.background9,
            R.drawable.background10,
            R.drawable.background11,
            R.drawable.background12,
            R.drawable.background13,
            R.drawable.background14,
            R.drawable.background15,
            R.drawable.background16,
            R.drawable.background17,
            R.drawable.background18,
            R.drawable.background19,
            R.drawable.background20,
            R.drawable.background21,
            R.drawable.background22,
            R.drawable.background23,
            R.drawable.background24,
            R.drawable.background25,
            R.drawable.background26,
            R.drawable.background27,
            R.drawable.background28,
            R.drawable.background29,
            R.drawable.background30,
            R.drawable.background31,
            R.drawable.background32,
            R.drawable.background33,
            R.drawable.background34,
            R.drawable.background35,
            R.drawable.background36)
    private var backgrounds: ArrayList<Int>? = null
    private var fonts: ArrayList<String>? = null

    private var pagerAdapter: ScreenSlidePagerAdapter? = null

    private var presenter: QuotesSlideContract.Presenter? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quotes_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mLanguagePreferences = new LanguagePreferences(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        loadBackgrounds(savedInstanceState)
        loadFonts(savedInstanceState)

        // Workaround to prevent crash android.os.FileUriExposedException: file:///storage/emulated/0/Android/data ...
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        presenter?.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putIntegerArrayList(BACKGROUNDS, backgrounds)
            putStringArrayList(FONTS, fonts)
            putSerializable(QUOTES, presenter!!.quotes as Serializable)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            presenter?.quotes = savedInstanceState.getSerializable(QUOTES) as List<Quote>
        }
    }

    override fun setPresenter(presenter: QuotesSlideContract.Presenter) {
        this.presenter = presenter
    }

    private fun loadFonts(savedInstanceState: Bundle?) {
        // Load fonts
        try {
            if (savedInstanceState == null) {
                val fontsList = activity!!.assets.list("font")
                fonts = ArrayList(listOf(*fontsList!!))
                fonts?.shuffle()
            } else {
                Log.d(TAG, "Loading fonts from bundle")
                fonts = savedInstanceState.getStringArrayList(FONTS)
            }
        } catch (e: IOException) {
            throw Error("Unable to open fonts")
        }

    }

    private fun loadBackgrounds(savedInstanceState: Bundle?) {
        // Load backgrounds
        if (savedInstanceState == null) {
            Log.d(TAG, "Loading and shuffling backgrounds")

            backgrounds = ArrayList(availableBackgrounds.size)
            for (i in availableBackgrounds.indices) {
                backgrounds?.add(availableBackgrounds[i])
            }
            backgrounds?.shuffle()
        } else {
            Log.d(TAG, "Loading backgrounds from bundle")
            backgrounds = savedInstanceState.getIntegerArrayList(BACKGROUNDS)
        }
    }

    fun shuffle() {
        pagerAdapter?.shuffle()
    }

    override fun initialize() {
        if (pagerAdapter == null) { // TODO is this check necessary?
            pagerAdapter = ScreenSlidePagerAdapter(activity!!.supportFragmentManager, backgrounds!!, fonts!!, presenter!!)
            pager?.apply {
                adapter = pagerAdapter
                setPageTransformer(true, ZoomOutPageTransformer())
            }
        }
    }

    override fun setQuotes(quotes: List<Quote>) {
        pagerAdapter?.setQuotes(quotes)
    }

    override fun showProgress(show: Boolean) {
        loader?.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun onLanguageChange() {
        presenter?.onLanguageChange()
    }

    private class ZoomOutPageTransformer : ViewPager.PageTransformer {

        companion object {
            private const val MIN_SCALE = 0.85f
            private const val MIN_ALPHA = 0.5f
        }

        override fun transformPage(view: View, position: Float) {
            val pageWidth = view.width
            val pageHeight = view.height

            when {
                position < -1 -> // [-Infinity,-1)
                    // getActivity() page is way off-screen to the left.
                    view.alpha = 0f
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as
                    // well
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                    val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                    val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                    if (position < 0) {
                        view.translationX = horizontalMargin - verticalMargin / 2
                    } else {
                        view.translationX = -horizontalMargin + verticalMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor

                    // Fade the page relative to its size.
                    view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                }
                else -> // (1,+Infinity]
                    // getActivity() page is way off-screen to the right.
                    view.alpha = 0f
            }
        }
    }
}
