package com.mn.tiger.datastorage.db.table;

import android.database.Cursor;

import com.mn.tiger.datastorage.db.converter.ColumnConverter;
import com.mn.tiger.datastorage.db.converter.ColumnConverterFactory;
import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Column
{
	private static final Logger LOG = Logger.getLogger(Column.class);

	protected final String columnName;
	private final Object defaultValue;

	protected Method getMethod;
	protected Method setMethod;

	protected final Field columnField;
	@SuppressWarnings("rawtypes")
	protected final ColumnConverter columnConverter;

	protected Column(Class<?> entityType, Field field)
	{
		this.columnField = field;
		this.columnConverter = ColumnConverterFactory.getColumnConverter(field.getType());
		this.columnName = ColumnUtils.getColumnNameByField(field);
		if (this.columnConverter != null)
		{
			this.defaultValue = this.columnConverter.getFiledValue(ColumnUtils
					.getColumnDefaultValue(field));
		}
		else
		{
			this.defaultValue = null;
		}
		this.getMethod = ColumnUtils.getColumnGetMethod(entityType, field);
		this.setMethod = ColumnUtils.getColumnSetMethod(entityType, field);
	}

	public void setValue2Entity(Object entity, Cursor cursor, int index)
	{

		Object value = columnConverter.getFiledValue(cursor, index);
		if (value == null && defaultValue == null)
			return;

		if (setMethod != null)
		{
			try
			{
				setMethod.invoke(entity, value == null ? defaultValue : value);
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
				this.columnField.set(entity, value == null ? defaultValue : value);
			}
			catch (Throwable e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Object getColumnValue(Object entity)
	{
		Object fieldValue = getFieldValue(entity);
		return columnConverter.fieldValue2ColumnValue(fieldValue);
	}

	public Object getFieldValue(Object entity)
	{
		Object fieldValue = null;
		if (entity != null)
		{
			if (getMethod != null)
			{
				try
				{
					fieldValue = getMethod.invoke(entity);
				}
				catch (Throwable e)
				{
					LOG.e("[Method:getFieldValue]", e);
				}
			}
			else
			{
				try
				{
					this.columnField.setAccessible(true);
					fieldValue = this.columnField.get(entity);
				}
				catch (Throwable e)
				{
					LOG.e("[Method:getFieldValue]", e);
				}
			}
		}
		return fieldValue;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public Field getColumnField()
	{
		return columnField;
	}

	public String getColumnDbType()
	{
		return columnConverter.getColumnDbType();
	}
}
