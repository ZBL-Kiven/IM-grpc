package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.SenderInfo;

public class SenderContentConverter {
    @TypeConverter
    public static SenderInfo revertSender(String value) {
        return new Gson().fromJson(value, SenderInfo.class);
    }

    @TypeConverter
    public static String convertSender(SenderInfo value) {
        return new Gson().toJson(value);
    }
}