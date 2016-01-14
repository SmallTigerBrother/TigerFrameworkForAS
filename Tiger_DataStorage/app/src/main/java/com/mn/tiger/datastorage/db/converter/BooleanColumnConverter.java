package com.mn.tiger.datastorage.db.converter;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * 
 * 表数据与对象属性(Boolean)类型转换类
 * 用于把对象中Boolean类型数据和表中INTEGER类型数据互转
 * 
 */
public class BooleanColumnConverter implements ColumnConverter<Boolean> {
    @Override
    public Boolean getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index) == 1;
    }

    @Override
    public Boolean getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return fieldStringValue.length() == 1 ? "1".equals(fieldStringValue) : Boolean.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Boolean fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue ? 1 : 0;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
