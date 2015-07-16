package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Integer)类型转换类
 * 用于把对象中Integer类型数据和表中Integer类型数据互转
 * 
 */
public class IntegerColumnConverter implements ColumnConverter<Integer> {
    @Override
    public Integer getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }

    @Override
    public Integer getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Integer.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Integer fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
