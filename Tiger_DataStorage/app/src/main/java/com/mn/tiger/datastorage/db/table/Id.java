package com.mn.tiger.datastorage.db.table;

import com.mn.tiger.datastorage.db.annotation.NoAutoIncrement;
import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;

public class Id extends Column
{
	private static final Logger LOG = Logger.getLogger(Id.class);

	protected Id(Class<?> entityType, Field field)
	{
		super(entityType, field);
		columnFieldClassName = columnField.getType().getCanonicalName();
	}

	private String columnFieldClassName;
	private boolean isAutoIncrementChecked = false;
	private boolean isAutoIncrement = false;

	public boolean isAutoIncrement()
	{
		if (!isAutoIncrementChecked)
		{
			isAutoIncrementChecked = true;
			isAutoIncrement = columnField.getAnnotation(NoAutoIncrement.class) == null
					&& AUTO_INCREMENT_TYPES.contains(columnFieldClassName);
		}
		return isAutoIncrement;
	}

	public void setAutoIncrementId(Object entity, long value)
	{
		Object idValue = value;
		if (INTEGER_TYPES.contains(columnFieldClassName))
		{
			idValue = (int) value;
		}

		if (setMethod != null)
		{
			try
			{
				setMethod.invoke(entity, idValue);
			}
			catch (Throwable e)
			{
				LOG.e("[Method]setAutoIncrementId", e);
			}
		}
		else
		{
			try
			{
				this.columnField.setAccessible(true);
				this.columnField.set(entity, idValue);
			}
			catch (Throwable e)
			{
				LOG.e("[Method]setAutoIncrementId", e);
			}
		}
	}

	@Override
	public Object getColumnValue(Object entity)
	{
		Object idValue = super.getColumnValue(entity);
		if (idValue != null)
		{
			if (this.isAutoIncrement() && (idValue.equals(0) || idValue.equals(0L)))
			{
				return null;
			}
			else
			{
				return idValue;
			}
		}
		return null;
	}

	private static final HashSet<String> INTEGER_TYPES = new HashSet<String>(2);
	private static final HashSet<String> AUTO_INCREMENT_TYPES = new HashSet<String>(4);

	static
	{
		INTEGER_TYPES.add(int.class.getCanonicalName());
		INTEGER_TYPES.add(Integer.class.getCanonicalName());

		AUTO_INCREMENT_TYPES.addAll(INTEGER_TYPES);
		AUTO_INCREMENT_TYPES.add(long.class.getCanonicalName());
		AUTO_INCREMENT_TYPES.add(Long.class.getCanonicalName());
	}
}
