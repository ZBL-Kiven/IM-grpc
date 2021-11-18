package com.zj.emotionbar.interfaces;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public interface ExtInflater<T> {

    void onInflate(FrameLayout view, LayoutInflater inflater, T data);
}
