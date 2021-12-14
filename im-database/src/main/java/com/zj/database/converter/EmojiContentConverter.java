package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.EmotionMessage;

public class EmojiContentConverter {

    @TypeConverter
    public static EmotionMessage revertEmoji(String value) {
        return new Gson().fromJson(value, EmotionMessage.class);
    }

    @TypeConverter
    public static String convertEmoji(EmotionMessage value) {
        return new Gson().toJson(value);
    }
}