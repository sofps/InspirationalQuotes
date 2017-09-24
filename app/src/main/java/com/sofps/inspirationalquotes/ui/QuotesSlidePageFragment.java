package com.sofps.inspirationalquotes.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sofps.inspirationalquotes.R;

public class QuotesSlidePageFragment extends Fragment implements QuotesSlidePageContract.View {

	@BindView(R.id.quote) TextView mTextViewQuote;
	@BindView(R.id.author) TextView mTextViewAuthor;

	private ViewGroup mRootView;

	public static final String ARG_PAGE = "page";
	public static final String ARG_BACKGROUND = "background";
	public static final String ARG_FONT = "font";
	public static final String ARG_QUOTE = "quote";
	public static final String ARG_AUTHOR = "author";

	private static final String AUTHOR_PREFIX = "-";

	private int mPageNumber;
	private int mBackground;
	private String mFont;
	private String mQuote;
	private String mAuthor;

	public static QuotesSlidePageFragment create(int pageNumber,
			int background, String font, String quote, String author) {
		QuotesSlidePageFragment fragment = new QuotesSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		args.putInt(ARG_BACKGROUND, background);
		args.putString(ARG_FONT, font);
		args.putString(ARG_QUOTE, quote);
		args.putString(ARG_AUTHOR, author);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		mPageNumber = getArguments().getInt(ARG_PAGE);
		mBackground = getArguments().getInt(ARG_BACKGROUND);
		mFont = getArguments().getString(ARG_FONT);
		mQuote = getArguments().getString(ARG_QUOTE);
		mAuthor = getArguments().getString(ARG_AUTHOR);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_quotes_slide_page, container, false);
		mRootView.setBackgroundResource(mBackground);

		ButterKnife.bind(this, mRootView);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				mFont);

		mTextViewQuote.setTypeface(font);
		mTextViewQuote.setText(mQuote);

		mTextViewAuthor.setTypeface(font);
		mTextViewAuthor.setText(AUTHOR_PREFIX + " " + mAuthor);

		float size;
		if ((mFont.contains("large") && mQuote.length() >= 50)) {
			size = getResources().getDimension(R.dimen.long_quote_size)
					/ getResources().getDisplayMetrics().density;
			mTextViewQuote.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
			mTextViewAuthor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		} else if (mFont.contains("small")) {
			size = getResources().getDimension(
					R.dimen.quote_size_for_smaller_fonts)
					/ getResources().getDisplayMetrics().density;
			mTextViewQuote.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
			mTextViewAuthor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		}

		return mRootView;
	}

	public int getPageNumber() {
		return mPageNumber;
	}

	@Override
	public void setPresenter(QuotesSlidePageContract.Presenter presenter) {

	}
}
