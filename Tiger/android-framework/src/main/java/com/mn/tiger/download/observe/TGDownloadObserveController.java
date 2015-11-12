package com.mn.tiger.download.observe;

import android.database.Observable;

import com.mn.tiger.download.observe.TGDownloadObservable.OnObserverChangeListener;
import com.mn.tiger.log.Logger;
import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 该类作用及功能说明: Observer控制器，负责注册、分发观察者
 *
 * @date 2014年3月31日
 */
public class TGDownloadObserveController
{
	private static final Logger LOG = Logger.getLogger(TGDownloadObserveController.class);

	/** instance */
	private static TGDownloadObserveController instance = new TGDownloadObserveController();

	/** observer container */
	private Map<String, Observable<TGDownloadObserver>> observerMap = null;

	/**
	 * ObserveControll structure function.
	 */
	private TGDownloadObserveController()
	{
		if (observerMap == null)
		{
			observerMap = new HashMap<String, Observable<TGDownloadObserver>>();
		}
	}

	/**
	 *
	 * 该方法的作用: 获取控制器实例
	 *
	 * @date 2014年3月31日
	 * @return ObserveControll
	 */
	public static synchronized TGDownloadObserveController getInstance()
	{
		if (instance == null)
		{
			instance = new TGDownloadObserveController();
		}
		return instance;
	}

	/**
	 *
	 * 该方法的作用: 根据传入的key，注册数据观察者
	 *
	 * @date 2014年3月31日
	 * @param key
	 * @param observer
	 */
	public void registerDataSetObserver(String key, TGDownloadObserver observer)
	{
		if (StringUtils.isEmptyOrNull(key) || observer == null)
		{
			LOG.e("[Method:registerDataSetObserver]register observer fail, key or observer is empty.");
			return;
		}

		observer.setKey(key);
		if (observerMap == null)
		{
			LOG.e("[Method:registerDataSetObserver]register observer fail, container map is null.");
			return;
		}

		if (!observerMap.containsKey(key) || observerMap.get(key) == null)
		{
			TGDownloadObservable newObservable = new TGDownloadObservable();
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
	 *
	 * 该方法的作用: 取消注册observer
	 *
	 * @date 2014年3月31日
	 * @param observer
	 */
	public void unregisterObserver(TGDownloadObserver observer)
	{
		String key = observer.getKey();
		if (StringUtils.isEmptyOrNull(key))
		{
			LOG.e("[Method:unregisterObserver] unregister observer fail, key is empty.");
			return;
		}

		if (observerMap == null)
		{
			LOG.e("[Method:unregisterObserver] unregister observer fail, container map is null.");
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
			LOG.e("[Method:unregisterObserverByKey] unregister observer fail, key is empty.");
			return;
		}

		if (observerMap == null)
		{
			LOG.e("[Method:unregisterObserverByKey] unregister observer fail, container map is null.");
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
	 * @param result
	 */
	public void notifyChange(TGTaskResult result)
	{
		LOG.d("[Method:notifyChange]");

		int taskId = result.getTaskID();

		Observable<TGDownloadObserver> observable = observerMap.get(String.valueOf(taskId));
		if(observable !=  null)
		{
			((TGDownloadObservable) observable).notifyChange(result.getResult());
		}
	}
}
