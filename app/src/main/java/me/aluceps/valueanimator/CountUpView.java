package me.aluceps.valueanimator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.Locale;

public class CountUpView extends View {

    private static final String FORMAT_SEPARATED = "%,3d";

    private static final String FORMAT_NOT_SEPARATED = "%d";

    private ValueAnimator point;

    private int current;

    private int maximum;

    private float textSize;

    private int textColor;

    public CountUpView(Context context) {
        this(context, null);
    }

    public CountUpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        current = 0;
        maximum = 0;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountUpView, 0, 0);

        try {
            textSize = a.getDimension(R.styleable.CountUpView_textSize, 0);
            textColor = a.getColor(R.styleable.CountUpView_textColor, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextAlign(Paint.Align.RIGHT);
        if (textSize > 0) {
            p.setTextSize(textSize);
        }
        if (textColor != 0) {
            p.setColor(textColor);
        }

        String v;
        if (point != null) {
            v = String.format(Locale.US, current > 1000 ? FORMAT_SEPARATED : FORMAT_NOT_SEPARATED, current);
        } else {
            v = String.format(Locale.US, FORMAT_NOT_SEPARATED, 0);
        }

        float textWidth = p.measureText(v);
        float width = (canvas.getWidth() + textWidth) / 2;
        float height = canvas.getHeight() / 2 - (p.descent() + p.ascent()) / 2;
        canvas.drawText(v, width, height, p);
    }

    public void set(int value, int duration) {
        maximum = value;
        point = ValueAnimator.ofInt(0, value);
        point.setDuration(duration);
        point.setInterpolator(new AccelerateInterpolator());
        point.addUpdateListener(valueAnimator -> {
            current = (int) valueAnimator.getAnimatedValue();
            if (current >= maximum) {
                current = maximum;
            }
            invalidate();
            requestLayout();
        });
        point.start();
    }
}
