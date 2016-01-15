package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;

/**
 * 
 * 表数据与对象属性(String)类型转换类
 * 用于把对象中String类型数据和表中TEXT类型数据互转
 * 
 */
public class StringColumnConverter implements ColumnConverter<String>
{
    @Override
    public String getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public String getFiledValue(String fieldStringValue) {
        return fieldStringValue;
    }

    @Override
    public Object fieldValue2ColumnValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "TEXT";
    }
}
