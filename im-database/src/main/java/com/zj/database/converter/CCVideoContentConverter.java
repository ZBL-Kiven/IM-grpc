package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.CCVideoContent;

public class CCVideoContentConverter {
    @TypeConverter
    public static CCVideoContent revertCCVideo(String value) {
        return new Gson().fromJson(value, CCVideoContent.class);
    }

    @TypeConverter
    public static String convertCCVideo(CCVideoContent value) {
        return new Gson().toJson(value);
    }
}