package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;

/**
 * 
 * 列数据与对象属性数据互转接口
 * 
 */
public interface ColumnConverter<T> {

    T getFiledValue(final Cursor cursor, int index);

    T getFiledValue(String fieldStringValue);

    Object fieldValue2ColumnValue(T fieldValue);

    String getColumnDbType();
}
