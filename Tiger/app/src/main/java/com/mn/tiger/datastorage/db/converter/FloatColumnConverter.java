package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Float)类型转换类
 * 用于把对象中Float类型数据和表中REAL类型数据互转
 * 
 */
public class FloatColumnConverter implements ColumnConverter<Float> {
    @Override
    public Float getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getFloat(index);
    }

    @Override
    public Float getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Float.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Float fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "REAL";
    }
}
