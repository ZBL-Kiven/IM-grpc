package com.zj.emotionbar.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zj.emotionbar.R;
import com.zj.emotionbar.utils.EmoticonsKeyboardUtils;

@SuppressWarnings("unused")
public abstract class AutoHeightLayout extends SoftKeyboardSizeWatchLayout implements SoftKeyboardSizeWatchLayout.OnResizeListener {

    private static final int ID_CHILD = R.id.im_emotion_auto_layout;

    protected Context context;
    protected int maxParentHeight;
    protected int softKeyboardHeight;
    protected boolean configurationChangedFlag = false;

    public AutoHeightLayout(Context context) {
        this(context, null, 0);
    }

    public AutoHeightLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoHeightLayout(Context context, AttributeSet attrs, int def) {
        super(context, attrs);
        this.context = context;
        softKeyboardHeight = EmoticonsKeyboardUtils.getDefKeyboardHeight(this.context);
        addOnResizeListener(this);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childSum = getChildCount();
        if (childSum > 1) {
            throw new IllegalStateException("can host only one direct child");
        }
        super.addView(child, index, params);
        if (childSum == 0) {
            if (child.getId() != 0) {
                child.setId(ID_CHILD);
            }
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ALIGN_PARENT_BOTTOM);
            child.setLayoutParams(paramsChild);
        } else if (childSum == 1) {
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ABOVE, ID_CHILD);
            child.setLayoutParams(paramsChild);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onSoftKeyboardHeightChanged(softKeyboardHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        if (maxParentHeight == 0) {
            maxParentHeight = h;
        }
    }

    public void updateMaxParentHeight(int maxParentHeight) {
        this.maxParentHeight = maxParentHeight;
        if (maxParentHeightChangeListener != null) {
            maxParentHeightChangeListener.onMaxParentHeightChange(maxParentHeight);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configurationChangedFlag = true;
        mScreenHeight = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (configurationChangedFlag) {
            configurationChangedFlag = false;
            Rect r = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            if (mScreenHeight == 0) {
                mScreenHeight = r.bottom;
            }
            maxParentHeight = mScreenHeight - r.bottom;
        }

        if (maxParentHeight != 0) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(maxParentHeight, heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSoftKeyboardPop(final int height) {
        if (softKeyboardHeight != height) {
            softKeyboardHeight = height;
            EmoticonsKeyboardUtils.setDefKeyboardHeight(context, softKeyboardHeight);
            onSoftKeyboardHeightChanged(softKeyboardHeight);
        }
    }

    @Override
    public void onSoftKeyboardClose() {
    }

    public abstract void onSoftKeyboardHeightChanged(int height);

    private OnMaxParentHeightChangeListener maxParentHeightChangeListener;

    public interface OnMaxParentHeightChangeListener {
        void onMaxParentHeightChange(int height);
    }

    public void setOnMaxParentHeightChangeListener(OnMaxParentHeightChangeListener listener) {
        maxParentHeightChangeListener = listener;
    }
}