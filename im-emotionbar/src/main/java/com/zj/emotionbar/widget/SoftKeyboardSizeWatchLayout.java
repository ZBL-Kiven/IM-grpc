package com.zj.emotionbar.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class SoftKeyboardSizeWatchLayout extends RelativeLayout {

    private final Context mContext;
    private int mOldHeight = -1;
    private int mNowHeight = -1;
    protected int mScreenHeight = 0;
    protected boolean mIsSoftKeyboardPop = false;

    public SoftKeyboardSizeWatchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

            if (mScreenHeight == 0) {
                mScreenHeight = r.bottom;
            }

            mNowHeight = mScreenHeight - r.bottom;

            if (mOldHeight != -1 && mNowHeight != mOldHeight) {
                if (mNowHeight > 0) {
                    mIsSoftKeyboardPop = true;
                    if (mListenerList != null) {
                        for (OnResizeListener l : mListenerList) {
                            l.onSoftKeyboardPop(mNowHeight);
                        }
                    }
                } else {
                    mIsSoftKeyboardPop = false;
                    if (mListenerList != null) {
                        for (OnResizeListener l : mListenerList) {
                            l.onSoftKeyboardClose();
                        }
                    }
                }
            }
            mOldHeight = mNowHeight;
        });
    }

    public boolean isSoftKeyboardPop() {
        return mIsSoftKeyboardPop;
    }

    private List<OnResizeListener> mListenerList;

    public void addOnResizeListener(OnResizeListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }

        mListenerList.add(l);
    }

    public interface OnResizeListener {
        /**
         * ???????????????
         * @param height?????????????????????
         */
        void onSoftKeyboardPop(int height);

        /**
         * ???????????????
         */
        void onSoftKeyboardClose();
    }
}