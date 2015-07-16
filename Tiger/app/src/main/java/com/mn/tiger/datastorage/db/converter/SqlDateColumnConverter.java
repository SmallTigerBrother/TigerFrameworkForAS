package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(java.sql.Date)类型转换类
 * 用于把对象中java.sql.Date类型数据和表中Integer类型数据互转
 * 
 */
public class SqlDateColumnConverter implements ColumnConverter<java.sql.Date> {
    @Override
    public java.sql.Date getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new java.sql.Date(cursor.getLong(index));
    }

    @Override
    public java.sql.Date getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return new java.sql.Date(Long.valueOf(fieldStringValue));
    }

    @Override
    public Object fieldValue2ColumnValue(java.sql.Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
