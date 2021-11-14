package com.zj.emotionbar.adapt2cc;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface OnKeyboardListener<T> {

    void onPictureClick(View view, @Nullable T extData);

    void onTakePhotoClick(View view, @Nullable T extData);

    void onSelectVideoClick(View view, @Nullable T extData);

    void onSelectFileClick(View view, @Nullable T extData);

    void onStickerClick(@NonNull String url, View view, @Nullable T extData);

    void sendText(@NonNull String content, @Nullable T extData);

    void onVoiceEvent(View view, MotionEvent ev, @Nullable T extData);

}
