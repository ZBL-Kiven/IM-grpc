package com.zj.emotionbar.adapt2cc;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zj.emotionbar.data.Emoticon;
import com.zj.emotionbar.data.EmoticonPack;

public interface OnKeyboardListener<T,E extends Emoticon> {

    void onPayClick(@Nullable EmoticonPack<E> emoticonPack);

    void onRetryClick(@Nullable EmoticonPack<E> emoticonPack);

    void sendSticker(@NonNull E emoticon , View view, @Nullable T extData);

    void sendText(@NonNull String content, @Nullable T extData);

    void onVoiceEvent(View view, MotionEvent ev, @Nullable T extData);

    void onPageEmoticonSelected(@Nullable EmoticonPack<E> emoticonPack);
}
