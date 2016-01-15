package com.mn.tiger.datastorage.db.table;

import android.database.Cursor;

import com.google.gson.Gson;
import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;

public class ColumnObject extends Column
{
	private static final Logger LOG = Logger.getLogger(ColumnObject.class);

	protected ColumnObject(Class<?> entityType, Field field)
	{
		super(entityType, field);
	}

	@Override
	public void setValue2Entity(Object entity, Cursor cursor, int index)
	{
		Object value = null;
		Class<?> columnType = columnField.getType();
		value = new Gson().fromJson(cursor.getString(index), columnType);

		if (setMethod != null)
		{
			try
			{
				setMethod.invoke(entity, value);
			}
			catch (Throwable e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
		else
		{
			try
			{
				this.columnField.setAccessible(true);
				this.columnField.set(entity, value);
			}
			catch (Throwable e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
	}

	@Override
	public Object getColumnValue(Object entity)
	{
		return getFieldValue(entity);
	}

}
