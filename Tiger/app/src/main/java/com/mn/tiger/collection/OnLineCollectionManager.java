package com.mn.tiger.collection;

import android.content.Context;

import java.lang.reflect.Array;

/**
 * 在线收藏管理类
 */
public abstract class OnLineCollectionManager<T extends ICollectable> implements
		ICollectionManager<T>
{
	@SuppressWarnings("unchecked")
	@Override
	public void insertCollection(Context context, T collectInfo,
			final OnInsertCallback callback)
	{
		//提交一条数据时，调用批量提交的方法
		T[] collectInfos = (T[]) Array.newInstance(collectInfo.getClass(), 1);
		collectInfos[0] = collectInfo;
		insertCollection(context, collectInfos, callback);
	}
}
