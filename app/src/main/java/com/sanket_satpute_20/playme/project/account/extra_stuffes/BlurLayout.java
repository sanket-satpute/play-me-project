package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BlurLayout extends View {

    private Paint paint;
    private Rect rect;

    public BlurLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize paint object
        paint = new Paint();

        // Set blur effect
        BlurMaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        paint.setMaskFilter(blur);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the layout
        rect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawRect(rect, paint);
    }
}
