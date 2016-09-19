package io.vithor.htmltextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Vithorio Polten on 25/08/16.
 */

public class HtmlTextView extends TextView implements Justify.Justified {


    // ======= Justify SPAN Attributes ===>
    private static final int MAX_SPANS = 512;

    private boolean mMeasuring = false;

    private Typeface mTypeface = null;
    private float mTextSize = 0f;
    private float mTextScaleX = 0f;
    private boolean mFakeBold = false;
    private int mWidth = 0;

    private int[] mSpanStarts = new int[MAX_SPANS];
    private int[] mSpanEnds = new int[MAX_SPANS];
    private Justify.ScaleSpan[] mSpans = new Justify.ScaleSpan[MAX_SPANS];
    // <== Justify SPAN Attributes =======||


    public HtmlTextView(Context context) {
        super(context);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
    }

    public void setHtmlText(@NonNull CharSequence text) {
        HtmlTagFormatter htmlTagFormatter = new HtmlTagFormatter();
        try {
            Spanned spanned = htmlTagFormatter.handlerHtmlContent(getContext(), text.toString());
            setText(spanned);
        } catch (Exception ex) {
            Log.d("HtmlTextView", "Failed to parse HTML", ex);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                setText(Html.fromHtml(text.toString()));
            }
        }
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Make sure we don't call setupScaleSpans again if the measure was triggered
        // by setupScaleSpans itself.
        if (!mMeasuring) {
            final Typeface typeface = getTypeface();
            final float textSize = getTextSize();
            final float textScaleX = getTextScaleX();
            final boolean fakeBold = getPaint().isFakeBoldText();
            if (mTypeface != typeface ||
                    mTextSize != textSize ||
                    mTextScaleX != textScaleX ||
                    mFakeBold != fakeBold) {
                final int width = MeasureSpec.getSize(widthMeasureSpec);
                if (width > 0 && width != mWidth) {
                    mTypeface = typeface;
                    mTextSize = textSize;
                    mTextScaleX = textScaleX;
                    mFakeBold = fakeBold;
                    mWidth = width;
                    mMeasuring = true;
                    try {
                        // Setup ScaleXSpans on whitespaces to justify the text.
                        Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans);
                    }
                    finally {
                        mMeasuring = false;
                    }
                }
            }
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text,
                                 final int start, final int lengthBefore, final int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        final Layout layout = getLayout();
        if (layout != null) {
            Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans);
        }
    }

    @Override
    @NotNull
    public TextView getTextView() {
        return this;
    }

    @Override
    public float getMaxProportion() {
        return Justify.DEFAULT_MAX_PROPORTION;
    }
}