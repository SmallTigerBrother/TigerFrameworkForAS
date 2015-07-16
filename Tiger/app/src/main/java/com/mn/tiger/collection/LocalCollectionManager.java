package com.mn.tiger.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.sqlite.Selector;
import com.mn.tiger.datastorage.db.sqlite.WhereBuilder;
import com.mn.tiger.log.Logger;

/**
 * 本地收藏管理类
 */
public class LocalCollectionManager<T extends ICollectable> implements ICollectionManager<T>
{
	private static final Logger LOG = Logger.getLogger(LocalCollectionManager.class);
	
	/**
	 * 收藏数据类名
	 */
	private Class<T> collectableClazz;
	
	/**
	 * 排序列名
	 */
	private String orderColumnName;
	
	/**
	 * 数据库名
	 */
	private String dbName;
	
	/**
	 * id列名
	 */
	private String idColumnName;
	
	public LocalCollectionManager(Class<T> collectableClazz,String dbName, String idColumnName)
	{
		this.collectableClazz = collectableClazz;
		this.dbName = dbName;
		this.idColumnName = idColumnName;
	}
	
	/**
	 * 获取数据库管理类
	 * @param context
	 * @return
	 */
	private TGDBManager getDBManager(Context context)
	{
		return TGDBManager.create(context, dbName, 1, null);
	}
	
	/**
	 * 设置排序列名
	 * @param orderColumnName
	 */
	public void setOrderColumnName(String orderColumnName)
	{
		this.orderColumnName = orderColumnName;
	}
	
	@Override
	public void insertCollection(Context context, T collectInfo,
			IModifyCollectionCallback callback)
	{
		try
		{
			getDBManager(context).saveOrUpdate(collectInfo);
			if(null != callback)
			{
				callback.onSuccess(OPERATE_INSERT);
			}
		}
		catch (DbException e)
		{
			LOG.e("[Method:insertLocalCollection] " + e.getMessage());
			if(null != callback)
			{
				callback.onError(ERROR_INSERT_LOCAL_COLLECTION, e.getMessage());
			}
		}
	}

	@Override
	public void insertCollection(Context context, T[] collectInfos,
			IModifyCollectionCallback callback)
	{
		try
		{
			getDBManager(context).saveOrUpdateAll(Arrays.asList(collectInfos));
			if(null != callback)
			{
				callback.onSuccess(OPERATE_INSERT);
			}
		}
		catch (DbException e)
		{
			LOG.e("[Method:insertLocalCollection] " + e.getMessage());
			if(null != callback)
			{
				callback.onError(ERROR_INSERT_LOCAL_COLLECTION, e.getMessage());
			}
		}
	}
	
	@Override
	public void removeCollection(Context context, Object value, 
			IModifyCollectionCallback callback)
	{
		try
		{
			getDBManager(context).delete(collectableClazz, 
						WhereBuilder.b(idColumnName, "=", value));
			if(null != callback)
			{
				callback.onSuccess(OPERATE_REMOVE);
			}
		}
		catch (DbException e)
		{
			LOG.e("[Method:removeLocalCollection] " + e.getMessage());
			if(null != callback)
			{
				callback.onError(ERROR_REMOVE_LOCAL_COLLECTION, e.getMessage());
			}
		}
	}
	
	@Override
	public boolean isCollected(Context context, T collectInfo)
	{
		return null != findLocalCollection(context, collectInfo.getCollectId());
	}

	@Override
	public void getCollections(Context context, int pageNo, int pageSize,
			IQueryCollectionsCallback<List<T>> callback)
	{
		List<T> allProductInfos = findAllLocalCollectionsDesc(context);
		//pageNo从1开始
		int startIndex = (pageNo - 1) * pageSize;
		int endIndex = pageNo * pageSize;
		
		//若起始索引大于所有收藏的个数，直接返回null
		if(startIndex > allProductInfos.size())
		{
			//调用回调
			if(null != callback)
			{
				callback.onSuccess(new ArrayList<T>());
			}
			return;
		}
		
		//若终止索引大于所有收藏的个数，设置终止索引为所有收藏的个数
		if(endIndex > allProductInfos.size())
		{
			endIndex = allProductInfos.size();
		}
		
		//调用回调
		if(null != callback)
		{
			callback.onSuccess(allProductInfos.subList(startIndex, endIndex));
		}
	}

	@Override
	public void getCollections(Context context, Object lastCollectionId, int pageSize, IQueryCollectionsCallback<Object> callback)
	{

	}

	/**
	 * 查找所有收藏信息,按收藏时间逆序排列
	 * @param context
	 * @return 无数据时，返回结果的size == 0
	 */
	public List<T> findAllLocalCollectionsDesc(Context context)
	{
		try
		{
			//逆序排列
			return getDBManager(context).findAll(Selector.from(collectableClazz).orderBy(
					orderColumnName, true));
		}
		catch (DbException e)
		{
			LOG.e("[Method:findAllLocalCollectionsdesc] " + e.getMessage());
		}
		
		return new ArrayList<T>();
	}

	/**
	 * 通过id查找收藏信息
	 * @param context
	 * @param idValue 收藏ID
	 * @return 找不到数据时，返回null
	 */
	public T findLocalCollection(Context context, Object idValue)
	{
		try
		{
			return getDBManager(context).findFirst(collectableClazz, 
					WhereBuilder.b(idColumnName, "=", idValue));
		}
		catch (DbException e)
		{
			LOG.e("[Method:findLocalCollection] " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * 查找所有收藏信息，按收藏时间顺序排列
	 * @param context
	 * @return 无数据时，返回结果的size == 0
	 */
	public List<T> findAllLocalCollections(Context context)
	{
		try
		{
			return getDBManager(context).findAll(Selector.from(collectableClazz).orderBy(
					orderColumnName, false));
		}
		catch (DbException e)
		{
			LOG.e("[Method:findAllLocalCollections] " + e.getMessage());
		}
		
		return new ArrayList<T>();
	}
	
	/**
	 * 清空本地收藏数据
	 * @param context
	 */
	public void clearLocalCollections(Context context)
	{
		try
		{
			getDBManager(context).deleteAll(collectableClazz);
		}
		catch (DbException e)
		{
			LOG.e("[Method:clearLocalCollections] " + e.getMessage());
		}
	}
}
