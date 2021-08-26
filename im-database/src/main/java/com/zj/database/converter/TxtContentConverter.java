package com.zj.database.converter;

import androidx.room.TypeConverter;

import com.zj.database.entity.TextContent;

public class TxtContentConverter {
    @TypeConverter
    public static TextContent revertText(String value) {
        TextContent content = new TextContent();
        content.setText(value);
        return content;
    }

    @TypeConverter
    public static String convertText(TextContent value) {
        return value == null ? null : value.getText();
    }
}