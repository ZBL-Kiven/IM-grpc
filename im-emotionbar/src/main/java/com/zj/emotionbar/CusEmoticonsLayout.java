package com.zj.emotionbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zj.emotionbar.adpater.EmoticonPacksAdapter;
import com.zj.emotionbar.data.Emoticon;
import com.zj.emotionbar.data.EmoticonPack;
import com.zj.emotionbar.interfaces.EmoticonsFuncListener;
import com.zj.emotionbar.interfaces.EmoticonsToolBar;
import com.zj.emotionbar.interfaces.ExtInflater;
import com.zj.emotionbar.interfaces.OnToolBarItemClickListener;
import com.zj.emotionbar.utils.EmoticonsKeyboardUtils;
import com.zj.emotionbar.widget.AutoHeightLayout;
import com.zj.emotionbar.widget.EmoticonsEditText;
import com.zj.emotionbar.widget.EmoticonsFuncView;
import com.zj.emotionbar.widget.FuncLayout;

import java.util.List;

@SuppressWarnings("unused")
public class CusEmoticonsLayout<T, E extends Emoticon> extends AutoHeightLayout implements View.OnClickListener, EmoticonsFuncListener<E>, OnToolBarItemClickListener<E>, EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {

    public static final int FUNC_TYPE_EMOTION = -1;
    public static final int FUNC_TYPE_APS = -2;

    protected LayoutInflater inflater;
    protected ImageView btnVoiceOrText;
    protected LinearLayout mLlRootView;
    protected TextView btnVoice;
    protected EmoticonsEditText emoticonsEditText;
    protected ImageView btnFace;
    protected RelativeLayout inputLayout;
    protected ImageView btnMultimedia;
    protected Button btnSend;
    protected FrameLayout extContainer;
    protected FuncLayout funcLayout;
    private ExtInflater<T> extInflater;
    private T extData;
    private boolean showKeyboardIfLayoutFinish = false;

    protected EmoticonsFuncView<E> emoticonsFuncView;
    protected EmoticonsToolBar<E> emoticonsToolBar;
    private TextView mTvBlocked;
    private EmoticonsFuncListener<E> mEmoticonsFuncListener;
    protected boolean dispatchKeyEventPreImeLock = false;
    private boolean isBlocked = false;
    private int viewModel = 1;

    public CusEmoticonsLayout(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public CusEmoticonsLayout(Context context, AttributeSet attrs, int def) {
        super(context, attrs, def);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initFuncView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getY() < getChildAt(0).getTop()) reset();
        return super.dispatchTouchEvent(ev);
    }

    protected void inflateKeyboardBar() {
        inflater.inflate(R.layout.view_keyboard_xhs, this);
    }

    @SuppressLint("InflateParams")
    protected View inflateFunc() {
        return inflater.inflate(R.layout.view_func_emoticon, null);
    }

    protected void initView() {
        btnVoiceOrText = findViewById(R.id.im_input_btn_voice_or_text);
        mTvBlocked = findViewById(R.id.im_input_tv_blocked);
        btnVoice = findViewById(R.id.im_input_btn_voice);
        emoticonsEditText = findViewById(R.id.im_input_et_chat);
        btnFace = findViewById(R.id.im_input_btn_face);
        inputLayout = findViewById(R.id.im_input_rl_input);
        btnMultimedia = findViewById(R.id.im_input_btn_multimedia);
        btnSend = findViewById(R.id.im_input_btn_send);
        extContainer = findViewById(R.id.im_input_ext_container);
        funcLayout = findViewById(R.id.im_input_key_board_func);
        mLlRootView = findViewById(R.id.im_emotion_ll_root_view);
        btnVoiceOrText.setOnClickListener(this);
        btnFace.setOnClickListener(this);
        btnMultimedia.setOnClickListener(this);
        emoticonsEditText.setOnBackKeyClickListener(this);
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initEditView();
    }

    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        funcLayout.addFuncView(FUNC_TYPE_EMOTION, keyboardView);
        emoticonsFuncView = findViewById(R.id.view_epv);
        emoticonsToolBar = findViewById(R.id.view_etv);
        emoticonsFuncView.setListener(this);
        emoticonsToolBar.setToolBarItemClickListener(this);
        funcLayout.setOnFuncChangeListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initEditView() {
        emoticonsEditText.setOnTouchListener((v, event) -> {
            if (!emoticonsEditText.isFocused()) {
                emoticonsEditText.setFocusable(true);
                emoticonsEditText.setFocusableInTouchMode(true);
                emoticonsEditText.requestFocus();
            }
            return false;
        });

        emoticonsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    btnSend.setVisibility(VISIBLE);
                    btnMultimedia.setVisibility(GONE);
                } else {
                    btnSend.setVisibility(GONE);
                    if (viewModel == 0) {
                        btnMultimedia.setVisibility(GONE);
                    } else {
                        btnMultimedia.setVisibility(VISIBLE);
                    }
                }
            }
        });
    }

    public void setAdapter(@NonNull EmoticonPacksAdapter<E> adapter) {
        List<EmoticonPack<E>> packList = adapter.getPackList();
        emoticonsToolBar.setPackList(packList);
        emoticonsFuncView.setAdapter(adapter);
        adapter.setAdapterListener(() -> emoticonsToolBar.notifyDataChanged());
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
        if (isBlocked) {
            mTvBlocked.setVisibility(View.VISIBLE);
            reset();
        } else {
            mTvBlocked.setVisibility(View.GONE);
        }
    }

    public void setOnPageEmoticonSelectedListener(EmoticonsFuncListener<E> listener) {
        mEmoticonsFuncListener = listener;
    }

    public void setViewNormaModel() {
        viewModel = 0;
        btnVoiceOrText.setVisibility(View.GONE);
        btnMultimedia.setVisibility(View.GONE);
    }

    public void setExtInflater(ExtInflater<T> inflater) {
        this.extInflater = inflater;
    }

    public void addFuncView(View view) {
        funcLayout.addFuncView(FUNC_TYPE_APS, view);
    }

    public void reset() {
        emoticonsEditText.clearFocus();
        emoticonsEditText.setFocusable(true);
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        funcLayout.hideAllFuncView();
        btnFace.setImageResource(R.mipmap.ui_emo_icon_face_pop);
    }

    public void setExtData(final T data) {
        this.extData = data;
        extContainer.removeAllViews();
        if (data != null) {
            if (extInflater != null) extInflater.onInflate(extContainer, inflater, data);
            if (!emoticonsEditText.isFocused()) post(() -> {
                if (inputLayout.isShown()) {
                    EmoticonsKeyboardUtils.openSoftKeyboard(emoticonsEditText);
                }
            });
        }
    }

    public void setScrollerView(RecyclerView view) {
        funcLayout.setScrollerView(view);
    }

    protected void showVoice() {
        inputLayout.setVisibility(GONE);
        btnVoice.setVisibility(VISIBLE);
        reset();
    }

    protected void checkVoice() {
        if (btnVoice.isShown()) {
            btnVoiceOrText.setImageResource(R.mipmap.ui_emo_icon_keyboard);
        } else {
            btnVoiceOrText.setImageResource(R.mipmap.ui_emo_icon_voice);
        }
    }

    protected void showText() {
        inputLayout.setVisibility(VISIBLE);
        btnVoice.setVisibility(GONE);
    }

    protected void toggleFuncView(int key) {
        showText();
        funcLayout.toggleFuncView(key, isSoftKeyboardPop(), emoticonsEditText);
    }

    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOTION == key) {
            btnFace.setImageResource(R.mipmap.ui_emo_icon_face);
        } else {
            btnFace.setImageResource(R.mipmap.ui_emo_icon_face_pop);
        }
        checkVoice();
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) funcLayout.getLayoutParams();
        params.height = height;
        funcLayout.setLayoutParams(params);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        funcLayout.updateHeight(height);
    }

    @Override
    public void onSoftKeyboardPop(int height) {
        super.onSoftKeyboardPop(height);
        showKeyboardIfLayoutFinish = false;
        funcLayout.setVisibility(true);
        setExtData(extData);
        funcLayout.resetKey();
        onFuncChange(funcLayout.NONE_KEY);
        int h = getChildAt(0).getHeight();
        funcLayout.changePaddingViewHeight();
    }

    @Override
    public void onSoftKeyboardClose() {
        super.onSoftKeyboardClose();
        if (funcLayout.isFuncHidden()) {
            reset();
        } else {
            onFuncChange(funcLayout.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(FuncLayout.FuncKeyBoardListener l) {
        funcLayout.addOnKeyBoardListener(l);
    }

    @Override
    public void onCurrentEmoticonPackChanged(EmoticonPack<E> currentPack) {
        emoticonsToolBar.selectEmotionPack(currentPack);
        if (mEmoticonsFuncListener != null) mEmoticonsFuncListener.onCurrentEmoticonPackChanged(currentPack);
    }

    @Override
    public void onPageSelected(int position) {
        if (mEmoticonsFuncListener != null) mEmoticonsFuncListener.onPageSelected(position);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (isBlocked) return;
        if (i == R.id.im_input_btn_voice_or_text) {
            if (inputLayout.isShown()) {
                btnVoiceOrText.setImageResource(R.mipmap.ui_emo_icon_keyboard);
                showVoice();
            } else {
                showText();
                btnVoiceOrText.setImageResource(R.mipmap.ui_emo_icon_voice);
                if (!EmoticonsKeyboardUtils.openSoftKeyboard(emoticonsEditText)) {
                    showKeyboardIfLayoutFinish = true;
                }
            }
        } else if (i == R.id.im_input_btn_face) {
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == R.id.im_input_btn_multimedia) {
            toggleFuncView(FUNC_TYPE_APS);
        }
    }

    @Override
    public void onToolBarItemClick(@NonNull EmoticonPack<E> pack) {
        emoticonsFuncView.setCurrentPageSet(pack);
        emoticonsToolBar.selectEmotionPack(pack);
    }

    @Override
    public void onBackKeyClick() {
        if (funcLayout.isShown()) {
            dispatchKeyEventPreImeLock = true;
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (dispatchKeyEventPreImeLock) {
                dispatchKeyEventPreImeLock = false;
                return true;
            }
            if (funcLayout.isShown()) {
                reset();
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return false;
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext()) && funcLayout.isShown()) {
                reset();
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            boolean isFocused;
            isFocused = emoticonsEditText.getShowSoftInputOnFocus();
            if (isFocused) {
                emoticonsEditText.onKeyDown(event.getKeyCode(), event);
            }
        }
        return false;
    }

    public EmoticonsEditText getEtChat() {
        return emoticonsEditText;
    }

    public TextView getBtnVoice() {
        return btnVoice;
    }

    public Button getBtnSend() {
        return btnSend;
    }

    public EmoticonsFuncView<E> getEmoticonsFuncView() {
        return emoticonsFuncView;
    }

    public TextView getBlockedView() {
        return mTvBlocked;
    }

    public T takeExtData() {
        return extData;
    }

    public EmoticonsToolBar<E> getEmoticonsToolBarView() {
        return emoticonsToolBar;
    }

    public View getChatInputRootView() {
        return mLlRootView;
    }
}
