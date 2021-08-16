package com.zj.database.converter;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.zj.database.entity.AudioContent;

public class AudioContentConverter {
    @TypeConverter
    public static AudioContent revertAudio(String value) {
        return new Gson().fromJson(value, AudioContent.class);
    }

    @TypeConverter
    public static String convertAudio(AudioContent value) {
        return new Gson().toJson(value);
    }
}