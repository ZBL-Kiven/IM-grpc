package com.zj.emotionbar.epack.emoticon;

import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.zj.emotionbar.data.Emoticon;
import com.zj.emotionbar.interfaces.OnEmoticonClickListener;


public abstract class OnEmojiClickListener<E extends Emoticon> implements OnEmoticonClickListener<E> {

    public abstract EditText getEt();

    public abstract void onStickerClick(E emoticon, View view);

    @Override
    public void onEmoticonClick(E emoticon, View v) {
        if (emoticon == null) {
            return;
        }

        if (emoticon instanceof EmoticonEntityUtils.DeleteEmoticon) {
            int action = KeyEvent.ACTION_DOWN;
            int code = KeyEvent.KEYCODE_DEL;
            KeyEvent event = new KeyEvent(action, code);
            getEt().onKeyDown(KeyEvent.KEYCODE_DEL, event);
        } else if (emoticon instanceof EmoticonEntityUtils.BigEmoticon) {
            onStickerClick(emoticon, v);
        } else {
            String content = emoticon.getIcon();
            if (TextUtils.isEmpty(content)) {
                return;
            }
            int index = getEt().getSelectionStart();
            Editable editable = getEt().getText();
            editable.insert(index, content);
        }
    }
}
