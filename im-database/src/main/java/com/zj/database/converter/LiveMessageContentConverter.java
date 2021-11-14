package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.LiveMessageEntity;


public class LiveMessageContentConverter {

    @TypeConverter
    public static LiveMessageEntity revertImg(String value) {
        return new Gson().fromJson(value, LiveMessageEntity.class);
    }

    @TypeConverter
    public static String convertImg(LiveMessageEntity value) {
        return new Gson().toJson(value);
    }
}
