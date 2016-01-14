package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Character)类型转换类
 * 用于把对象中Character类型数据和表中INTEGER类型数据互转
 * 
 */
public class CharColumnConverter implements ColumnConverter<Character> {
    @Override
    public Character getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (char) cursor.getInt(index);
    }

    @Override
    public Character getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return fieldStringValue.charAt(0);
    }

    @Override
    public Object fieldValue2ColumnValue(Character fieldValue) {
        if (fieldValue == null) return null;
        return (int) fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
