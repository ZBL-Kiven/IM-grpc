package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.ImgContent;

public class ImgContentConverter {
    @TypeConverter
    public static ImgContent revertImg(String value) {
        return new Gson().fromJson(value, ImgContent.class);
    }

    @TypeConverter
    public static String convertImg(ImgContent value) {
        return new Gson().toJson(value);
    }
}