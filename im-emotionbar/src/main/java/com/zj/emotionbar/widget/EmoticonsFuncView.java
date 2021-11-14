package com.zj.emotionbar.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import com.zj.emotionbar.adpater.EmoticonPacksAdapter;
import com.zj.emotionbar.data.Emoticon;
import com.zj.emotionbar.data.EmoticonPack;


public class EmoticonsFuncView extends ViewPager {

    private static final String TAG = "EmoticonsFuncView";

    protected EmoticonPacksAdapter mAdapter;
    protected int mCurrentPagePosition;

    public EmoticonsFuncView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(EmoticonPacksAdapter adapter) {
        super.setAdapter(adapter);
        
        mAdapter = adapter;

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (Math.abs(positionOffset) < 0.000001) {
                    mCurrentPagePosition = position;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected position:" + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (mEmoticonsFuncListener == null
                || mAdapter.getCount() == 0) {
            return;
        }
        
        EmoticonPack<?> pack = mAdapter.getPackList().get(0);
        mEmoticonsFuncListener.onCurrentEmoticonPackChanged(pack);
    }

    public void setCurrentPageSet(EmoticonPack<? extends Emoticon> pack) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            return;
        }
        setCurrentItem(mAdapter.getEmoticonPackPosition(pack));
    }

    private EmoticonsFuncListener mEmoticonsFuncListener;

    public void setListener(EmoticonsFuncListener listener) {
        mEmoticonsFuncListener = listener;
    }


    public interface EmoticonsFuncListener {

        void onCurrentEmoticonPackChanged(EmoticonPack<? extends Emoticon> currentPack);
    }
}
