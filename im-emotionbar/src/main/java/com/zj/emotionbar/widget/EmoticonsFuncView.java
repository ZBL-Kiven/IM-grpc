package com.zj.emotionbar.widget;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;

import com.zj.emotionbar.adpater.EmoticonPacksAdapter;
import com.zj.emotionbar.data.Emoticon;
import com.zj.emotionbar.data.EmoticonPack;
import com.zj.emotionbar.interfaces.EmoticonsFuncListener;


public class EmoticonsFuncView<E extends Emoticon> extends ViewPager {

    private static final String TAG = "EmoticonsFuncView";

    protected EmoticonPacksAdapter<E> mAdapter;
    protected int mCurrentPagePosition;
    private EmoticonsFuncListener<E> mEmoticonsFuncListener;

    public EmoticonsFuncView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(EmoticonPacksAdapter<E> adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected position:" + position);
                if (mCurrentPagePosition == position) return;
                mCurrentPagePosition = position;
                EmoticonPack<E> pack = mAdapter.getPackList().get(position);
                mEmoticonsFuncListener.onCurrentEmoticonPackChanged(pack);
                mEmoticonsFuncListener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (mEmoticonsFuncListener == null || mAdapter.getCount() == 0) {
            return;
        }

        EmoticonPack<E> pack = mAdapter.getPackList().get(0);
        mEmoticonsFuncListener.onCurrentEmoticonPackChanged(pack);
    }

    public void setCurrentPageSet(EmoticonPack<E> pack) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            return;
        }
        setCurrentItem(mAdapter.getEmoticonPackPosition(pack));
    }


    public void setListener(EmoticonsFuncListener<E> listener) {
        mEmoticonsFuncListener = listener;
    }
}
