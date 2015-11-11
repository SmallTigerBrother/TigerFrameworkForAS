package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Short)类型转换类
 * 用于把对象中Short类型数据和表中Integer类型数据互转
 * 
 */
public class ShortColumnConverter implements ColumnConverter<Short> {
    @Override
    public Short getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getShort(index);
    }

    @Override
    public Short getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Short.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Short fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
