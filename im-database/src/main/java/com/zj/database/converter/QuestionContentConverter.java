package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.QuestionContent;

public class QuestionContentConverter {
    @TypeConverter
    public static QuestionContent revertImg(String value) {
        return new Gson().fromJson(value, QuestionContent.class);
    }

    @TypeConverter
    public static String convertImg(QuestionContent value) {
        return new Gson().toJson(value);
    }
}