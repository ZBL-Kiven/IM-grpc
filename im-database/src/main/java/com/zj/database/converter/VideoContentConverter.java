package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.VideoContent;

public class VideoContentConverter {
    @TypeConverter
    public static VideoContent revertVideo(String value) {
        return new Gson().fromJson(value, VideoContent.class);
    }

    @TypeConverter
    public static String convertVideo(VideoContent value) {
        return new Gson().toJson(value);
    }
}