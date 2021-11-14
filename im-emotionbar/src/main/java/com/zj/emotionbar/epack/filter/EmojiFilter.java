package com.zj.emotionbar.epack.filter;

//package com.zj.emotionbar.epack.filter;
//
//import android.text.Spannable;
//import android.widget.EditText;
//import com.zj.ui.emotionboard.interfaces.EmoticonFilter;
//import com.zj.ui.emotionboard.utils.EmoticonsKeyboardUtils;
//
//@SuppressWarnings("SpellCheckingInspection")
//public class EmojiFilter extends EmoticonFilter {
//
//    private int emojiSize = -1;
//
//    @Override
//    public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
//        emojiSize = emojiSize == -1 ? EmoticonsKeyboardUtils.getFontHeight(editText) : emojiSize;
//        clearSpan(editText.getText(), start, text.toString().length());
//        Matcher m = EmojiDisplay.getMatcher(text.toString().substring(start, text.toString().length()));
//        while (m.find()) {
//            String emojiHex = Integer.toHexString(Character.codePointAt(m.group(), 0));
//                EmojiDisplay.emojiDisplay(editText.getContext(), editText.getText(), emojiHex, emojiSize, start + m.start(), start + m.end());
//        }
//    }
//
//    private void clearSpan(Spannable spannable, int start, int end) {
//        if (start == end) {
//            return;
//        }
//        EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
//        for (EmojiSpan oldSpan : oldSpans) {
//            spannable.removeSpan(oldSpan);
//        }
//    }
//}
