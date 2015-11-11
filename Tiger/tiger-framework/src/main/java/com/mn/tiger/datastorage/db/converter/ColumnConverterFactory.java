package com.mn.tiger.datastorage.db.converter;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.mn.tiger.datastorage.db.table.ColumnUtils;

/**
 * 
 * 列数据与对象属性数据互转工厂 用于预先加载列转换类
 * 
 */
@SuppressWarnings("rawtypes")
public class ColumnConverterFactory
{
	private ColumnConverterFactory()
	{
	}

	public static ColumnConverter getColumnConverter(Class columnType)
	{
		if (columnType_columnConverter_map.containsKey(columnType.getCanonicalName()))
		{
			return columnType_columnConverter_map.get(columnType.getCanonicalName());
		}
		else if(!ColumnUtils.isDbPrimitiveType(columnType))
		{
			return new StringColumnConverter();
		}
		else if (ColumnConverter.class.isAssignableFrom(columnType))
		{
			try
			{
				ColumnConverter columnConverter = (ColumnConverter) columnType.newInstance();
				if (columnConverter != null)
				{
					columnType_columnConverter_map.put(columnType.getCanonicalName(),
							columnConverter);
				}
				return columnConverter;
			}
			catch (Throwable e)
			{
			}
		}
		return null;
	}

	public static String getDbColumnType(Class columnType)
	{
		ColumnConverter converter = getColumnConverter(columnType);
		if (converter != null)
		{
			return converter.getColumnDbType();
		}
		return "TEXT";
	}

	public static void registerColumnConverter(Class columnType, ColumnConverter columnConverter)
	{
		columnType_columnConverter_map.put(columnType.getCanonicalName(), columnConverter);
	}

	public static boolean isSupportColumnConverter(Class columnType)
	{
		if (columnType_columnConverter_map.containsKey(columnType.getCanonicalName()))
		{
			return true;
		}
		else if (ColumnConverter.class.isAssignableFrom(columnType))
		{
			try
			{
				ColumnConverter columnConverter = (ColumnConverter) columnType.newInstance();
				if (columnConverter != null)
				{
					columnType_columnConverter_map.put(columnType.getCanonicalName(),
							columnConverter);
				}
				return columnConverter == null;
			}
			catch (Throwable e)
			{
			}
		}
		return false;
	}

	private static final ConcurrentHashMap<String, ColumnConverter> columnType_columnConverter_map;

	static
	{
		columnType_columnConverter_map = new ConcurrentHashMap<String, ColumnConverter>();

		BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
		columnType_columnConverter_map
				.put(boolean.class.getCanonicalName(), booleanColumnConverter);
		columnType_columnConverter_map
				.put(Boolean.class.getCanonicalName(), booleanColumnConverter);

		ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
		columnType_columnConverter_map.put(byte[].class.getCanonicalName(),
				byteArrayColumnConverter);

		ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
		columnType_columnConverter_map.put(byte.class.getCanonicalName(), byteColumnConverter);
		columnType_columnConverter_map.put(Byte.class.getCanonicalName(), byteColumnConverter);

		CharColumnConverter charColumnConverter = new CharColumnConverter();
		columnType_columnConverter_map.put(char.class.getCanonicalName(), charColumnConverter);
		columnType_columnConverter_map.put(Character.class.getCanonicalName(), charColumnConverter);

		DateColumnConverter dateColumnConverter = new DateColumnConverter();
		columnType_columnConverter_map.put(Date.class.getCanonicalName(), dateColumnConverter);

		DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
		columnType_columnConverter_map.put(double.class.getCanonicalName(), doubleColumnConverter);
		columnType_columnConverter_map.put(Double.class.getCanonicalName(), doubleColumnConverter);

		FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
		columnType_columnConverter_map.put(float.class.getCanonicalName(), floatColumnConverter);
		columnType_columnConverter_map.put(Float.class.getCanonicalName(), floatColumnConverter);

		IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
		columnType_columnConverter_map.put(int.class.getCanonicalName(), integerColumnConverter);
		columnType_columnConverter_map
				.put(Integer.class.getCanonicalName(), integerColumnConverter);

		LongColumnConverter longColumnConverter = new LongColumnConverter();
		columnType_columnConverter_map.put(long.class.getCanonicalName(), longColumnConverter);
		columnType_columnConverter_map.put(Long.class.getCanonicalName(), longColumnConverter);

		ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
		columnType_columnConverter_map.put(short.class.getCanonicalName(), shortColumnConverter);
		columnType_columnConverter_map.put(Short.class.getCanonicalName(), shortColumnConverter);

		SqlDateColumnConverter sqlDateColumnConverter = new SqlDateColumnConverter();
		columnType_columnConverter_map.put(java.sql.Date.class.getCanonicalName(),
				sqlDateColumnConverter);

		StringColumnConverter stringColumnConverter = new StringColumnConverter();
		columnType_columnConverter_map.put(String.class.getCanonicalName(), stringColumnConverter);
	}
}
