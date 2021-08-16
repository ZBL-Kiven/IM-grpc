package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.zj.database.entity.TextContent;

public class TxtContentConverter {
    @TypeConverter
    public static TextContent revertText(String value) {
        return new TextContent(value);
    }

    @TypeConverter
    public static String convertText(TextContent value) {
        return value.getText();
    }
}