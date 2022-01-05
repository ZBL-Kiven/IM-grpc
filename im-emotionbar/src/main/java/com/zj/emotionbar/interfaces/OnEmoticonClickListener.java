package com.zj.emotionbar.interfaces;

import android.view.View;

import com.zj.emotionbar.data.Emoticon;

public interface OnEmoticonClickListener<E extends Emoticon> {
    void onEmoticonClick(E t, View v);
}
