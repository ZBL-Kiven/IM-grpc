package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ExtContentConverter {

    @TypeConverter
    public static Map<String, String> revertImg(String value) {
        Type t = new TypeToken<Map<String, String>>() {}.getType();
        return new Gson().fromJson(value, t);
    }

    @TypeConverter
    public static String convertImg(Map<String, String> value) {
        return new Gson().toJson(value);
    }
}