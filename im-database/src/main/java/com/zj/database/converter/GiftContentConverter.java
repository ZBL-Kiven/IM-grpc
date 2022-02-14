package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.EmotionMessage;
import com.zj.database.entity.GiftMessage;

public class GiftContentConverter {

    @TypeConverter
    public static GiftMessage revertGift(String value) {
        return new Gson().fromJson(value, GiftMessage.class);
    }

    @TypeConverter
    public static String convertGift(GiftMessage value) {
        return new Gson().toJson(value);
    }
}