package com.zj.emotionbar.interfaces;

import android.view.LayoutInflater;
import android.view.View;

public interface ExtInflater<T> {

    void onInflate(View view, LayoutInflater inflater, T data);
}
