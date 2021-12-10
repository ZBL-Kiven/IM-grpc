package com.zj.emotionbar.adapt2cc.func;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.GridView;

import androidx.core.content.ContextCompat;

import com.zj.emotionbar.R;

import java.util.ArrayList;

public class FuncGridView extends GridView {

    public static final int FUNC_ITEM_ID_PIC = 0;
    public static final int FUNC_ITEM_ID_TAKE_PIC = 1;
    public static final int FUNC_ITEM_ID_VIDEO = 2;
    public static final int FUNC_ITEM_ID_FILE = 3;

    public FuncGridView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public FuncGridView(Context context, FuncAdapter.OnItemClickListener listener) {
        super(context);
        setNumColumns(4);
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_emotions_bg));
        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalSpacing(getDp(30));
        int padding = getDp(10);
        setHorizontalSpacing(padding);
        setVerticalScrollBarEnabled(false);
        setPadding(padding, padding * 3, padding, padding * 3);
        init(listener);
    }

    protected void init(FuncAdapter.OnItemClickListener listener) {
        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppBean(FUNC_ITEM_ID_PIC, R.mipmap.app_emo_func_icon_photo, getContext().getString(R.string.im_func_album)));
        mAppBeanList.add(new AppBean(FUNC_ITEM_ID_TAKE_PIC, R.mipmap.app_emo_func_icon_camera, getContext().getString(R.string.im_func_shot)));
        mAppBeanList.add(new AppBean(FUNC_ITEM_ID_VIDEO, R.mipmap.app_emo_func_icon_audio, getContext().getString(R.string.im_func_live)));
//        mAppBeanList.add(new AppBean(FUNC_ITEM_ID_FILE, R.mipmap.app_emo_func_icon_file, "文件"));
        FuncAdapter adapter = new FuncAdapter(getContext(), mAppBeanList, listener);
        setAdapter(adapter);
    }

    private int getDp(int i) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * i + 0.5f);
    }
}
