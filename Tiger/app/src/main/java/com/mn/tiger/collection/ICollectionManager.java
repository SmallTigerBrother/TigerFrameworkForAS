package com.mn.tiger.collection;

import android.content.Context;

import java.util.List;

/**
 * 收藏管理接口
 */
public interface ICollectionManager<T extends ICollectable>
{
	public interface OnInsertCallback
	{
		void onInsertSuccess();

		void onInsertError(int code, String message);
	}

	public interface OnRemoveCallback
	{
		void onRemoveSuccess();

		void onRemoveError(int code, String message);
	}

	/**
	 * 查询收藏回调接口
	 */
	public static interface OnQueryCallback<E>
	{
		/**
		 * 成功回调方法
		 * @param collectInfos 查询结果
		 */
		void onQuerySuccess(E collectInfos);

		void onQueryError(int code, String message);
	}

	/**
	 * 提交收藏
	 * @param context
	 * @param collectInfo 收藏信息
	 * @param callback 回调方法
	 */
	public void insertCollection(Context context, T collectInfo, OnInsertCallback callback);

	/**
	 * 批量提交收藏
	 * @param context
	 * @param collectInfos 收藏信息
	 * @param callback 回调方法
	 */
	public void insertCollection(Context context, T[] collectInfos, OnInsertCallback callback);

	/**
	 * 删除收藏
	 * @param context
	 * @param idValue id
	 * @param callback 回调方法
	 */
	public void removeCollection(Context context, Object idValue, OnRemoveCallback callback);

	/**
	 * 获取收藏列表
	 * @param context
	 * @param pageNo
	 * @param pageSize
	 * @param callback
	 */
	public void getCollections(Context context, int pageNo, int pageSize,
							   OnQueryCallback<List<T>> callback);

	/**
	 * 获取收藏列表
	 * @param context
	 * @param lastCollectionId
	 * @param pageSize
	 * @param callback
	 */
	public <E> void getCollections(Context context, Object lastCollectionId, int pageSize,
								   OnQueryCallback<E> callback);

	/**
	 * 判断数据是否已收藏
	 * @param context
	 * @param collectInfo
	 * @return
	 */
	public boolean isCollected(Context context, T collectInfo);

}
