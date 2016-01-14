package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;

/**
 * 
 * 表数据与对象属性(byte[])类型转换类
 * 用于把对象中byte[]类型数据和表中BLOB类型数据互转
 * 
 */
public class ByteArrayColumnConverter implements ColumnConverter<byte[]>
{
    @Override
    public byte[] getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }

    @Override
    public byte[] getFiledValue(String fieldStringValue) {
        return null;
    }

    @Override
    public Object fieldValue2ColumnValue(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "BLOB";
    }
}
