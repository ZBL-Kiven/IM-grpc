package com.zj.emotionbar.interfaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class EmoticonFilter {

    public abstract void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter);

    protected static Drawable getDrawableFromAssets(Context context, String emoticonName) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getAssets().open(emoticonName));
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static Drawable getDrawable(Context context, int emoticon) {
        if (emoticon <= 0) {
            return null;
        }
        return ContextCompat.getDrawable(context, emoticon);
    }
}
