package io.vithor.htmltextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Vithorio Polten on 25/08/16.
 */

public class HtmlTextView extends TextView {

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

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_LEGACY));
//            } else {
//                //noinspection deprecation
                setText(Html.fromHtml(text.toString()));
//            }
        }
    }
}
