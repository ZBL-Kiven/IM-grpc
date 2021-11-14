package com.zj.emotionbar.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.zj.emotionbar.utils.EmoticonsKeyboardUtils;

public class FuncLayout extends LinearLayout {

    public final int NONE_KEY = Integer.MIN_VALUE;

    private final SparseArray<View> mFuncViewArrayMap = new SparseArray<>();

    private int mCurrentFuncKey = NONE_KEY;

    protected int mHeight = 0;

    public FuncLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void addFuncView(int key, View view) {
        if (mFuncViewArrayMap.get(key) != null) {
            return;
        }
        mFuncViewArrayMap.put(key, view);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, params);
        view.setVisibility(GONE);
    }

    public void hideAllFuncView() {
        for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
            int keyTemp = mFuncViewArrayMap.keyAt(i);
            mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
        }

        mCurrentFuncKey = NONE_KEY;
        setVisibility(false);
    }

    public void toggleFuncView(int key, boolean isSoftKeyboardPopped, EditText editText) {

        if (isSoftKeyboardPopped) {
            closeSoftKeyboard(editText);
        }

        if (getCurrentFuncKey() == key) {
            if (!isSoftKeyboardPopped) {
                EmoticonsKeyboardUtils.openSoftKeyboard(editText);
            }
        } else {
            showFuncView(key);
        }
    }

    private void closeSoftKeyboard(EditText editText) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            EmoticonsKeyboardUtils.closeSoftKeyboard(editText);
        } else {
            EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
        }
    }

    public void showFuncView(int key) {
        if (mFuncViewArrayMap.get(key) != null) {
            for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
                int keyTemp = mFuncViewArrayMap.keyAt(i);
                if (keyTemp == key) {
                    mFuncViewArrayMap.get(keyTemp).setVisibility(VISIBLE);
                } else {
                    mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
                }
            }
        }
        mCurrentFuncKey = key;
        setVisibility(true);
        if (onFuncChangeListener != null) {
            onFuncChangeListener.onFuncChange(mCurrentFuncKey);
        }
    }

    public int getCurrentFuncKey() {
        return mCurrentFuncKey;
    }

    public void resetKey() {
        mCurrentFuncKey = NONE_KEY;
    }

    public void updateHeight(int height) {
        this.mHeight = height;
    }

    public void setVisibility(boolean isVisible) {
        LayoutParams params = (LayoutParams) getLayoutParams();

        if (isVisible) {
            setVisibility(VISIBLE);
            params.height = mHeight;
            if (mListenerList != null) {
                for (FuncKeyBoardListener l : mListenerList) {
                    l.onFuncPop(mHeight);
                }
            }
        } else {
            setVisibility(GONE);
            params.height = 0;
            if (mListenerList != null) {
                for (FuncKeyBoardListener l : mListenerList) {
                    l.onFuncClose();
                }
            }
        }

        setLayoutParams(params);
    }

    public boolean isFuncHidden() {
        return mCurrentFuncKey == NONE_KEY;
    }

    private List<FuncKeyBoardListener> mListenerList;

    public void addOnKeyBoardListener(FuncKeyBoardListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

    public interface FuncKeyBoardListener {

        void onFuncPop(int height);

        void onFuncClose();
    }

    private OnFuncChangeListener onFuncChangeListener;

    public interface OnFuncChangeListener {
        void onFuncChange(int key);
    }

    public void setOnFuncChangeListener(OnFuncChangeListener listener) {
        onFuncChangeListener = listener;
    }
}