package com.mn.tiger.collection;

import java.util.List;

import android.content.Context;

/**
 * 收藏管理接口
 */
public interface ICollectionManager<T extends ICollectable>
{
	/**
	 * 本地数据插入异常
	 */
	public static final int ERROR_INSERT_LOCAL_COLLECTION = -1;
	
	/**
	 * 本地数据移除异常
	 */
	public static final int ERROR_REMOVE_LOCAL_COLLECTION = -2;
	
	/**
	 * 插入操作
	 */
	public static final int OPERATE_INSERT = 1;
	
	/**
	 * 删除操作
	 */
	public static final int OPERATE_REMOVE = 2;
	
	/**
	 * 编辑收藏回调接口
	 */
	public static interface IModifyCollectionCallback
	{
		/**
		 * 成功回调方法
		 * @param operate 操作类型
		 */
		void onSuccess(int operate);
		
		void onError(int code, String message);
	}
	
	/**
	 * 查询收藏回调接口
	 */
	public static interface IQueryCollectionsCallback<E>
	{
		/**
		 * 成功回调方法
		 * @param collectInfos 查询结果
		 */
		void onSuccess(E collectInfos);
		
		void onError(int code, String message);
	}
	
	/**
	 * 提交收藏
	 * @param context
	 * @param collectInfo 收藏信息
	 * @param callback 回调方法
	 */
	public void insertCollection(Context context, T collectInfo, 
			IModifyCollectionCallback callback);
	
	/**
	 * 批量提交收藏
	 * @param context
	 * @param collectInfos 收藏信息
	 * @param callback 回调方法
	 */
	public void insertCollection(Context context, T[] collectInfos, 
			IModifyCollectionCallback callback);
	
	/**
	 * 删除收藏
	 * @param context
	 * @param idValue id
	 * @param callback 回调方法
	 */
	public void removeCollection(Context context, Object idValue, 
			IModifyCollectionCallback callback);
	
	/**
	 * 获取收藏列表
	 * @param context
	 * @param pageNo
	 * @param pageSize
	 * @param callback
	 */
	public void getCollections(Context context, int pageNo, int pageSize, 
			IQueryCollectionsCallback<List<T>> callback);

	/**
	 * 获取收藏列表
	 * @param context
	 * @param lastCollectionId
	 * @param pageSize
	 * @param callback
	 */
	public void getCollections(Context context, Object lastCollectionId, int pageSize,
							   IQueryCollectionsCallback<Object> callback);

	/**
	 * 判断数据是否已收藏
	 * @param context
	 * @param collectInfo
	 * @return
	 */
	public boolean isCollected(Context context, T collectInfo);
	
}
