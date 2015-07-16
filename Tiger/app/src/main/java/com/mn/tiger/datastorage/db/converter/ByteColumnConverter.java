package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Byte)类型转换类
 * 用于把对象中Byte类型数据和表中INTEGER类型数据互转
 * 
 */
public class ByteColumnConverter implements ColumnConverter<Byte> {
    @Override
    public Byte getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (byte) cursor.getInt(index);
    }

    @Override
    public Byte getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Byte.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Byte fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
