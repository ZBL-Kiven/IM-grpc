package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.MessageInfoEntity;

public class MessageConverter {

    @TypeConverter
    public static MessageInfoEntity revertMsg(String value) {
        return new Gson().fromJson(value, MessageInfoEntity.class);
    }

    @TypeConverter
    public static String convertMsg(MessageInfoEntity value) {
        return new Gson().toJson(value);
    }
}