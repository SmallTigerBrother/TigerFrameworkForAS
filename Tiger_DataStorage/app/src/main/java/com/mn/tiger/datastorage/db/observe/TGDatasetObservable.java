package com.mn.tiger.datastorage.db.observe;

import android.database.Observable;

/**
 * 
 * 该类作用及功能说明：用一个url的观察者，（取消）注册观察者到arraylist
 * 
 * @date 2014年3月31日
 */
public class TGDatasetObservable extends Observable<TGDataSetObserver>
{
	/**
	 * 取消注册
	 */
	@Override
	public void unregisterObserver(TGDataSetObserver observer)
	{
		super.unregisterObserver(observer);
		if (mObservers == null || (mObservers != null && mObservers.size() == 0))
		{
			mOnObserverChangeListener.onObserverClear(observer.getUri());
		}
	}

	/**
	 * 取消注册传入url对应的所有观察者
	 */
	@Override
	public void unregisterAll()
	{
		String uri = mObservers.get(0).getUri();
		super.unregisterAll();
		mOnObserverChangeListener.onObserverClear(uri);
	}

	/**
	 * 
	 * 该方法的作用: 通知观察者
	 * 
	 * @date 2014年3月31日
	 */
	public void notifyChange()
	{
		for (TGDataSetObserver observer : mObservers)
		{
			observer.onChanged();
		}
	}

	/**
	 * 
	 * 该方法的作用: 判断该observer是否已经注册过，已经注册返回true，否则返回false
	 * 
	 * @date 2014年3月31日
	 * @param observer
	 * @return
	 */
	public boolean isExistObserver(TGDataSetObserver observer)
	{
		for (TGDataSetObserver mObserver : mObservers)
		{
			if (mObserver.getId().equals(observer.getId()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * 观察者集合是否被清空监听
	 */
	private OnObserverChangeListener mOnObserverChangeListener;

	/**
	 * 
	 * 该类作用及功能说明:
	 * 观察者集合是否被清空监听，当mObservers被清空时，回调onObserverClear方法，把该集合从观察者控制类中移除
	 * 
	 * @date 2014年3月31日
	 */
	public interface OnObserverChangeListener
	{

		/**
		 * 
		 * 该方法的作用: 清空观察者集合回调方法.
		 * 
		 * @date 2014年3月31日
		 * @param uri
		 *            观察者对应uri
		 */
		void onObserverClear(String uri);
	}

	/**
	 * 
	 * 该方法的作用: 设置观察者变化监听
	 * 
	 * @date 2014年3月31日
	 * @param l
	 */
	public void setOnObserverChangeListener(OnObserverChangeListener l)
	{
		mOnObserverChangeListener = l;
	}
}
