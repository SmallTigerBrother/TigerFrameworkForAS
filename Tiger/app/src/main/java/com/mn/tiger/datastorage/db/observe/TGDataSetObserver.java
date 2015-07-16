package com.mn.tiger.datastorage.db.observe;

import java.util.UUID;

import android.database.DataSetObserver;

/**
 * 该类作用及功能说明 ：数据观察者
 * 
 * @date 2014年3月31日
 */
public class TGDataSetObserver extends DataSetObserver
{
	/**
	 * id
	 */
	private String id;

	/**
	 * 注册observer时使用得uri
	 */
	public String uri;

	public TGDataSetObserver()
	{
		setId(UUID.randomUUID().toString());
	}

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
