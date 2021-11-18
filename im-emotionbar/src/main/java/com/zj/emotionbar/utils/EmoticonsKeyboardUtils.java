package com.zj.emotionbar.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

@SuppressWarnings("unused")
public class EmoticonsKeyboardUtils {

    private static final String EXTRA_DEF_KEYBOARD_HEIGHT = "DEF_KEYBOARD_HEIGHT";
    private static final int DEF_KEYBOARD_HEIGHT_WITH_DP = 300;
    private static int sDefKeyboardHeight = -1;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDisplayWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static int getDefKeyboardHeight(Context context) {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = dip2px(context, DEF_KEYBOARD_HEIGHT_WITH_DP);
        }
        int height = PreferenceManager.getDefaultSharedPreferences(context).getInt(EXTRA_DEF_KEYBOARD_HEIGHT, 0);
        return sDefKeyboardHeight = height > 0 && sDefKeyboardHeight != height ? height : sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if (sDefKeyboardHeight != height) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(EXTRA_DEF_KEYBOARD_HEIGHT, height).apply();
            EmoticonsKeyboardUtils.sDefKeyboardHeight = height;
        }
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    public static boolean openSoftKeyboard(EditText et) {
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) return inputManager.showSoftInput(et, 0);
        }
        return false;
    }

    public static void closeSoftKeyboard(Context context) {
        if (!(context instanceof Activity) || ((Activity) context).getCurrentFocus() == null) {
            return;
        }
        try {
            View view = ((Activity) context).getCurrentFocus();
            if (view == null) return;
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSoftKeyboard(View view) {
        if (view == null || view.getWindowToken() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
