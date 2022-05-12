package com.zj.imtest.ui.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.zj.imtest.R;
import com.zj.views.list.refresh.layout.api.RefreshKernel;
import com.zj.views.list.refresh.layout.api.RefreshLayoutIn;
import com.zj.views.list.refresh.layout.simple.SimpleComponent;

import java.lang.ref.WeakReference;

public class RefreshHeader extends SimpleComponent implements com.zj.views.list.refresh.layout.api.RefreshHeader {

    private final int color;
    private int regionColor = Color.WHITE;
    private float animPercent;
    private float percent;
    private boolean isDragging;
    private boolean isStanding;
    private ValueAnimator standAnim;
    private Drawable drawable;
    private Rect drawingRect;
    private float drawableDrawingRectSize;


    private Context getCtx() {
        return new WeakReference<>(super.getContext()).get();
    }

    public RefreshHeader(Context context) {
        this(context, null, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshHeader, defStyleAttr, 0);
        try {
            color = ta.getColor(R.styleable.RefreshHeader_foregroundColor, Color.WHITE);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (this.isDragging != isDragging) {
            postInvalidate();
        }
        this.isDragging = isDragging;
        this.percent = percent;
        this.isStanding = percent >= 1f;
        standAnim.end();
    }

    @Override
    public int onFinish(@androidx.annotation.NonNull RefreshLayoutIn refreshLayout, boolean success) {
        isStanding = false;
        standAnim.end();
        return super.onFinish(refreshLayout, success);
    }

    @Override
    public void onStartAnimator(@androidx.annotation.NonNull RefreshLayoutIn refreshLayout, int height, int maxDragHeight) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight);
        isStanding = true;
        postInvalidate();

    }

    @Override
    public void onInitialized(@androidx.annotation.NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        super.onInitialized(kernel, height, maxDragHeight);
        float DRAWABLE_SIZE = 24f;
        drawableDrawingRectSize = getCtx().getResources().getDisplayMetrics().density * DRAWABLE_SIZE + 0.5f;
        drawable = ContextCompat.getDrawable(getCtx(), R.drawable.rfl_loading_white);
        standAnim = ValueAnimator.ofFloat(0.0f, 369.9f).setDuration(1500);
        standAnim.setRepeatCount(ValueAnimator.INFINITE);
        standAnim.setRepeatMode(ValueAnimator.RESTART);
        standAnim.addUpdateListener(this::onAnimationUpdate);
        standAnim.start();
    }

    private void onAnimationUpdate(ValueAnimator animation) {
        animPercent = animation.getAnimatedFraction();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        initDynamicPaint();
        if (isDragging || isStanding) {
            float degree = isDragging ? percent : animPercent;
            canvas.rotate(degree * 359f, drawingRect.centerX(), drawingRect.centerY());
            drawable.draw(canvas);
            postInvalidate();
        }
        canvas.restore();
    }

    private void initDynamicPaint() {
        float size = drawableDrawingRectSize * Math.min(1.0f, percent);
        float left = getWidth() / 2f - (size / 2f) + 0.5f;
        float right = left + size;
        float top = (size / 2f) + getHeight() / 2f + 0.5f;
        float bottom = top + size;
        drawingRect = new Rect((int) left, (int) top, (int) right, (int) bottom);
        if (regionColor != color) {
            drawable = tintDrawable(drawable, color);
        }
        drawable.setAlpha((int) ((0.3f + (percent * 0.7f)) * 255f));
        drawable.setBounds(drawingRect);
    }

    private Drawable tintDrawable(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        regionColor = color;
        return wrappedDrawable;
    }
}
