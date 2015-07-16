package com.mn.tiger.datastorage.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.mn.tiger.datastorage.db.annotation.Check;
import com.mn.tiger.datastorage.db.annotation.NotNull;
import com.mn.tiger.datastorage.db.annotation.ColumnObject;
import com.mn.tiger.datastorage.db.annotation.Transient;
import com.mn.tiger.datastorage.db.annotation.Unique;
import com.mn.tiger.datastorage.db.converter.ColumnConverter;
import com.mn.tiger.datastorage.db.converter.ColumnConverterFactory;
import com.mn.tiger.datastorage.db.sqlite.ForeignLazyLoader;
import com.mn.tiger.datastorage.db.annotation.Column;
import com.mn.tiger.datastorage.db.annotation.Foreign;
import com.mn.tiger.datastorage.db.annotation.Id;
import com.mn.tiger.log.LogTools;

public class ColumnUtils
{

	private ColumnUtils()
	{
	}

	private static final HashSet<String> DB_PRIMITIVE_TYPES = new HashSet<String>(14);

	static
	{
		DB_PRIMITIVE_TYPES.add(int.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(long.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(short.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(byte.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(float.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(double.class.getCanonicalName());

		DB_PRIMITIVE_TYPES.add(Integer.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(Long.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(Short.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(Byte.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(Float.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(Double.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(String.class.getCanonicalName());
		DB_PRIMITIVE_TYPES.add(byte[].class.getCanonicalName());
	}

	public static boolean isDbPrimitiveType(Class<?> fieldType)
	{
		return DB_PRIMITIVE_TYPES.contains(fieldType.getCanonicalName());
	}

	public static Method getColumnGetMethod(Class<?> entityType, Field field)
	{
		String fieldName = field.getName();
		Method getMethod = null;
		if(field.isAnnotationPresent(ColumnObject.class))
		{
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1) + "Str";
			try
			{
				getMethod = entityType.getDeclaredMethod(methodName);
			}
			catch (NoSuchMethodException e)
			{
				LogTools.d(methodName + " not exist");
			}
			
			getMethod.setAccessible(true);
			return getMethod;
		}
		
		if (field.getType() == boolean.class)
		{
			getMethod = getBooleanColumnGetMethod(entityType, fieldName);
			getMethod.setAccessible(true);
			return getMethod;
		}
		
		if (getMethod == null)
		{
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			try
			{
				getMethod = entityType.getDeclaredMethod(methodName);
				getMethod.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				LogTools.d(methodName + " not exist");
			}
		}

		if (getMethod == null && !Object.class.equals(entityType.getSuperclass()))
		{
			return getColumnGetMethod(entityType.getSuperclass(), field);
		}
		
		return getMethod;
	}

	public static Method getColumnSetMethod(Class<?> entityType, Field field)
	{
		String fieldName = field.getName();
		Method setMethod = null;
		
		if(field.isAnnotationPresent(ColumnObject.class))
		{
			String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1) + "Str";
			try
			{
				setMethod = entityType.getDeclaredMethod(methodName);
				setMethod.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				LogTools.d(methodName + " not exist");
			}
			
			return setMethod;
		}
		
		if (field.getType() == boolean.class)
		{
			setMethod = getBooleanColumnSetMethod(entityType, field);
			setMethod.setAccessible(true);
		}
		
		if (setMethod == null)
		{
			String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			try
			{
				setMethod = entityType.getDeclaredMethod(methodName, field.getType());
				setMethod.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				LogTools.d(methodName + " not exist");
			}
		}

		if (setMethod == null && !Object.class.equals(entityType.getSuperclass()))
		{
			return getColumnSetMethod(entityType.getSuperclass(), field);
		}
		return setMethod;
	}

	public static String getColumnNameByField(Field field)
	{
		Column column = field.getAnnotation(Column.class);
		if (column != null && !TextUtils.isEmpty(column.column()))
		{
			return column.column();
		}

		Id id = field.getAnnotation(Id.class);
		if (id != null && !TextUtils.isEmpty(id.column()))
		{
			return id.column();
		}

		Foreign foreign = field.getAnnotation(Foreign.class);
		if (foreign != null && !TextUtils.isEmpty(foreign.column()))
		{
			return foreign.column();
		}

		ColumnObject propertyObject = field.getAnnotation(ColumnObject.class);
		if (propertyObject != null && propertyObject.column().trim().length() != 0)
		{
			return propertyObject.column();
		}

		return field.getName();
	}

	public static String getForeignColumnNameByField(Field field)
	{
		Foreign foreign = field.getAnnotation(Foreign.class);
		if (foreign != null)
		{
			return foreign.foreign();
		}

		return field.getName();
	}

	public static String getColumnDefaultValue(Field field)
	{
		Column column = field.getAnnotation(Column.class);
		if (column != null && !TextUtils.isEmpty(column.defaultValue()))
		{
			return column.defaultValue();
		}
		return null;
	}

	public static boolean isTransient(Field field)
	{
		return field.getAnnotation(Transient.class) != null;
	}

	public static boolean isForeign(Field field)
	{
		return field.getAnnotation(Foreign.class) != null;
	}

	public static boolean isUnique(Field field)
	{
		return field.getAnnotation(Unique.class) != null;
	}

	public static boolean isNotNull(Field field)
	{
		return field.getAnnotation(NotNull.class) != null;
	}
	
	public static boolean isPropertyObject(Field field)
	{
		return null != field.getAnnotation(ColumnObject.class);
	}

	/**
	 * @param field
	 * @return check.value or null
	 */
	public static String getCheck(Field field)
	{
		Check check = field.getAnnotation(Check.class);
		if (check != null)
		{
			return check.value();
		}
		else
		{
			return null;
		}
	}

	public static Class<?> getForeignEntityType(
			com.mn.tiger.datastorage.db.table.Foreign foreignColumn)
	{
		Class<?> result = foreignColumn.getColumnField().getType();
		if (result.equals(ForeignLazyLoader.class) || result.equals(List.class))
		{
			result = (Class<?>) ((ParameterizedType) foreignColumn.getColumnField()
					.getGenericType()).getActualTypeArguments()[0];
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object convert2DbColumnValueIfNeeded(final Object value)
	{
		Object result = value;
		if (value != null)
		{
			Class<?> valueType = value.getClass();
			if (!isDbPrimitiveType(valueType))
			{
				ColumnConverter converter = ColumnConverterFactory.getColumnConverter(valueType);
				if (converter != null)
				{
					result = converter.fieldValue2ColumnValue(value);
				}
				else
				{
					result = value;
				}
			}
		}
		return result;
	}

	private static boolean isStartWithIs(final String fieldName)
	{
		return fieldName != null && fieldName.startsWith("is");
	}

	@SuppressLint("DefaultLocale")
	private static Method getBooleanColumnGetMethod(Class<?> entityType, final String fieldName)
	{
		String methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		if (isStartWithIs(fieldName))
		{
			methodName = fieldName;
		}
		try
		{
			return entityType.getDeclaredMethod(methodName);
		}
		catch (NoSuchMethodException e)
		{
			LogTools.d(methodName + " not exist");
		}
		return null;
	}

	@SuppressLint("DefaultLocale")
	private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field)
	{
		String fieldName = field.getName();
		String methodName = null;
		if (isStartWithIs(field.getName()))
		{
			methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
		}
		else
		{
			methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		try
		{
			return entityType.getDeclaredMethod(methodName, field.getType());
		}
		catch (NoSuchMethodException e)
		{
			LogTools.d(methodName + " not exist");
		}
		return null;
	}

}
