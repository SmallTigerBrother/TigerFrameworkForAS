package com.mn.tiger.datastorage.db.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.mn.tiger.datastorage.db.table.TableUtils;

public class Selector
{

	protected Class<?> entityType;
	protected String tableName;

	protected WhereBuilder whereBuilder;
	protected List<OrderBy> orderByList;
	protected int limit = 0;
	protected int offset = 0;

	private Selector(Class<?> entityType)
	{
		this.entityType = entityType;
		this.tableName = TableUtils.getTableName(entityType);
	}

	public static Selector from(Class<?> entityType)
	{
		return new Selector(entityType);
	}

	public Selector where(WhereBuilder whereBuilder)
	{
		this.whereBuilder = whereBuilder;
		return this;
	}

	public Selector where(String columnName, String op, Object value)
	{
		this.whereBuilder = WhereBuilder.b(columnName, op, value);
		return this;
	}

	public Selector and(String columnName, String op, Object value)
	{
		this.whereBuilder.and(columnName, op, value);
		return this;
	}

	public Selector and(WhereBuilder where)
	{
		this.whereBuilder.expr("AND (" + where.toString() + ")");
		return this;
	}

	public Selector or(String columnName, String op, Object value)
	{
		this.whereBuilder.or(columnName, op, value);
		return this;
	}

	public Selector or(WhereBuilder where)
	{
		this.whereBuilder.expr("OR (" + where.toString() + ")");
		return this;
	}

	public Selector expr(String expr)
	{
		if (this.whereBuilder == null)
		{
			this.whereBuilder = WhereBuilder.b();
		}
		this.whereBuilder.expr(expr);
		return this;
	}

	public Selector expr(String columnName, String op, Object value)
	{
		if (this.whereBuilder == null)
		{
			this.whereBuilder = WhereBuilder.b();
		}
		this.whereBuilder.expr(columnName, op, value);
		return this;
	}

	public TGModelSelector groupBy(String columnName)
	{
		return new TGModelSelector(this, columnName);
	}

	public TGModelSelector select(String... columnExpressions)
	{
		return new TGModelSelector(this, columnExpressions);
	}

	public Selector orderBy(String columnName)
	{
		if (orderByList == null)
		{
			orderByList = new ArrayList<OrderBy>(2);
		}
		
		if(null != columnName)
		{
			orderByList.add(new OrderBy(columnName));
		}
		
		return this;
	}

	public Selector orderBy(String columnName, boolean desc)
	{
		if (orderByList == null)
		{
			orderByList = new ArrayList<OrderBy>(2);
		}
		
		if(null != columnName)
		{
			orderByList.add(new OrderBy(columnName, desc));
		}
		
		return this;
	}

	public Selector limit(int limit)
	{
		this.limit = limit;
		return this;
	}

	public Selector offset(int offset)
	{
		this.offset = offset;
		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("SELECT ");
		result.append("*");
		result.append(" FROM ").append(tableName);
		if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0)
		{
			result.append(" WHERE ").append(whereBuilder.toString());
		}
		if (orderByList != null)
		{
			for (int i = 0; i < orderByList.size(); i++)
			{
				result.append(" ORDER BY ").append(orderByList.get(i).toString());
			}
		}
		if (limit > 0)
		{
			result.append(" LIMIT ").append(limit);
			result.append(" OFFSET ").append(offset);
		}
		return result.toString();
	}

	public Class<?> getEntityType()
	{
		return entityType;
	}

	protected class OrderBy
	{
		private String columnName;
		private boolean desc;

		public OrderBy(String columnName)
		{
			this.columnName = columnName;
		}

		public OrderBy(String columnName, boolean desc)
		{
			this.columnName = columnName;
			this.desc = desc;
		}

		@Override
		public String toString()
		{
			return columnName + (desc ? " DESC" : " ASC");
		}
	}
}
