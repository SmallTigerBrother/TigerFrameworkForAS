package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Double)类型转换类
 * 用于把对象中Double类型数据和表中REAL类型数据互转
 * 
 */
public class DoubleColumnConverter implements ColumnConverter<Double> {
    @Override
    public Double getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    @Override
    public Double getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Double.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "REAL";
    }
}
