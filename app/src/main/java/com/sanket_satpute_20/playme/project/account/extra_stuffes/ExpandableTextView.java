package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class ExpandableTextView extends AppCompatTextView {

    private static final int MAX_LINES = 3;
    private static final String ELLIPSIS = "...";
    private static final String VIEW_MORE = "View More";

    private boolean isExpanded = false;

    public ExpandableTextView(Context context) {
        super(context);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                setText(getDisplayableText());
            }
        });
        setText(getDisplayableText());
    }

    private CharSequence getDisplayableText() {
        if (isExpanded) {
            return getText();
        } else {
            Layout layout = getLayout();
            if (layout != null && layout.getLineCount() > MAX_LINES) {
                int end = layout.getLineEnd(MAX_LINES - 1);
                CharSequence text = getText().subSequence(0, end - ELLIPSIS.length());
                SpannableStringBuilder builder = new SpannableStringBuilder(text)
                        .append(ELLIPSIS)
                        .append(VIEW_MORE);
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        isExpanded = true;
                        setText(getDisplayableText());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, builder.length() - VIEW_MORE.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            } else {
                return getText();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isExpanded && getLayout() != null && getLayout().getLineCount() > MAX_LINES) {
            Paint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.setTextSize(getTextSize());
            paint.setTypeface(getTypeface());
            String viewMoreText = VIEW_MORE;
            float x = getLayout().getLineRight(MAX_LINES - 1) - paint.measureText(viewMoreText);
            float y = getLayout().getLineBottom(MAX_LINES - 1) - paint.descent();
            canvas.drawText(viewMoreText, x, y, paint);
        }
    }
}

