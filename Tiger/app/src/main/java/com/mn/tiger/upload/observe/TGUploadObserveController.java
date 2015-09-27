package com.mn.tiger.upload.observe;

import android.database.Observable;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.upload.observe.TGUploadObservable.OnObserverChangeListener;
import com.mn.tiger.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 该类作用及功能说明: Observer控制器，负责注册、分发观察者
 * 
 * @date 2014年3月31日
 */
public class TGUploadObserveController
{
	/**
	 * log tag
	 */
	public static final String TAG = TGUploadObserveController.class.getSimpleName();

	/** instance */
	private static TGUploadObserveController instance = new TGUploadObserveController();

	/** observer container */
	private Map<String, Observable<TGUploadObserver>> observerMap = null;

	/**
	 * ObserveControll structure function.
	 */
	private TGUploadObserveController()
	{
		if (observerMap == null)
		{
			observerMap = new HashMap<String, Observable<TGUploadObserver>>();
		}
	}

	/**
	 * 
	 * 该方法的作用: 获取控制器实例
	 * 
	 * @date 2014年3月31日
	 * @return ObserveControll
	 */
	public static synchronized TGUploadObserveController getInstance()
	{
		if (instance == null)
		{
			instance = new TGUploadObserveController();
		}
		return instance;
	}

	/**
	 * 
	 * 该方法的作用: 根据传入的key，注册数据观察者
	 * 
	 * @date 2014年3月31日
	 * @param entityType
	 * @param observer
	 */
	public void registerDataSetObserver(String key, TGUploadObserver observer)
	{
		if (StringUtils.isEmptyOrNull(key) || observer == null)
		{
			LogTools.e(TAG, "register observer fail, key or observer is empty.");
			return;
		}

		observer.setKey(key);
		if (observerMap == null)
		{
			LogTools.e(TAG, "register observer fail, container map is null.");
			return;
		}

		if (!observerMap.containsKey(key) || observerMap.get(key) == null)
		{
			TGUploadObservable newObservable = new TGUploadObservable();
			newObservable.setOnObserverChangeListener(new OnObserverChangeListener()
			{
				@Override
				public void onObserverClear(String key)
				{
					observerMap.remove(key);
				}
			});
			observerMap.put(key, newObservable);
		}

		observerMap.get(key).registerObserver(observer);
	}

	/**
	 * 该方法的作用: 取消注册observer
	 * @date 2014年3月31日
	 * @param observer
	 */
	public void unregisterObserver(TGUploadObserver observer)
	{
		String key = observer.getKey();
		if (StringUtils.isEmptyOrNull(key))
		{
			LogTools.e(TAG, "unregister observer fail, key is empty.");
			return;
		}

		if (observerMap == null)
		{
			LogTools.e(TAG, "unregister observer fail, container map is null.");
			return;
		}

		if (observerMap.containsKey(key) && observerMap.get(key) != null)
		{
			observerMap.get(key).unregisterObserver(observer);
		}
	}

	/**
	 * 
	 * 该方法的作用: 取消注册observer
	 * 
	 * @date 2014年3月31日
	 * @param key
	 */
	public void unregisterObserverByKey(String key)
	{
		if (StringUtils.isEmptyOrNull(key))
		{
			LogTools.e(TAG, "unregister observer fail, key is empty.");
			return;
		}

		if (observerMap == null)
		{
			LogTools.e(TAG, "unregister observer fail, container map is null.");
			return;
		}

		if (observerMap.containsKey(key) && observerMap.get(key) != null)
		{
			observerMap.remove(key);
		}
	}
	
	/**
	 * 
	 * 该方法的作用: 通知key对应的所有观察者，数据发生改变
	 * 
	 * @date 2014年3月31日
	 * @param entityType
	 */
	public void notifyChange(TGTaskResult result)
	{
		LogTools.i(TAG, "[Method:notifyChange]");
		
		int taskId = result.getTaskID();
		
		Observable<TGUploadObserver> observable = observerMap.get(String.valueOf(taskId));
		if(observable != null)
		{
			((TGUploadObservable) observable).notifyChange(result.getResult());
		}
	}
}
