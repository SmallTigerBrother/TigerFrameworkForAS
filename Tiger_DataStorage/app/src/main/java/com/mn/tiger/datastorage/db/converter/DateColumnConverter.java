package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

import java.util.Date;

/**
 * 
 * 表数据与对象属性(Date)类型转换类
 * 用于把对象中Date类型数据和表中INTEGER类型数据互转
 * 
 */
public class DateColumnConverter implements ColumnConverter<Date>
{
    @Override
    public Date getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }

    @Override
    public Date getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return new Date(Long.valueOf(fieldStringValue));
    }

    @Override
    public Object fieldValue2ColumnValue(Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
