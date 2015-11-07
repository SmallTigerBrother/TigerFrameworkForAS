package com.mn.tiger.datastorage.db.observe;

import android.database.Observable;

import com.mn.tiger.datastorage.db.observe.TGDatasetObservable.OnObserverChangeListener;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * 该类作用及功能说明: Observer控制器，负责注册、分发观察者
 *
 * @date 2014年3月31日
 */
public class TGDataSetObserveController
{
	private static final Logger LOG = Logger.getLogger(TGDataSetObserveController.class);

	/** instance */
	private static TGDataSetObserveController instance = new TGDataSetObserveController();

	/** observer container */
	private Map<String, Observable<TGDataSetObserver>> observerMap = null;

	/**
	 * ObserveControll structure function.
	 */
	private TGDataSetObserveController()
	{
		if (observerMap == null)
		{
			observerMap = new HashMap<String, Observable<TGDataSetObserver>>();
		}
	}

	/**
	 *
	 * 该方法的作用: 获取控制器实例
	 *
	 * @date 2014年3月31日
	 * @return ObserveControll
	 */
	public static synchronized TGDataSetObserveController getInstance()
	{
		if (instance == null)
		{
			instance = new TGDataSetObserveController();
		}
		return instance;
	}

	/**
	 *
	 * 该方法的作用: 根据传入的url(实体bean对应的绝对路径，含包名)，注册数据观察者
	 *
	 * @date 2014年3月31日
	 * @param entityType
	 * @param observer
	 */
	public void registerDataSetObserver(Class<?> entityType, TGDataSetObserver observer)
	{
		if (entityType == null)
		{
			return;
		}

		String uri = entityType.getName();
		if (StringUtils.isEmptyOrNull(uri))
		{
			LOG.e("[Method:registerDataSetObserver] register observer fail, uri is empty.");
			return;
		}

		observer.setUri(uri);
		if (observerMap == null)
		{
			LOG.e("[Method:registerDataSetObserver] register observer fail, container map is null.");
			return;
		}

		if (!observerMap.containsKey(uri) || observerMap.get(uri) == null)
		{
			TGDatasetObservable newObservable = new TGDatasetObservable();
			newObservable.setOnObserverChangeListener(new OnObserverChangeListener()
			{
				@Override
				public void onObserverClear(String uri)
				{
					observerMap.remove(uri);
				}
			});
			observerMap.put(uri, newObservable);
		}

		if (!((TGDatasetObservable) observerMap.get(uri)).isExistObserver(observer))
		{
			observerMap.get(uri).registerObserver(observer);
		}
		else
		{
			LOG.e("[Method:registerDataSetObserver] this observer is registered already.");
		}

	}

	/**
	 *
	 * 该方法的作用: 取消注册observer
	 *
	 * @date 2014年3月31日
	 * @param observer
	 */
	public void unregisterObserver(TGDataSetObserver observer)
	{
		String uri = observer.getUri();
		if (StringUtils.isEmptyOrNull(uri))
		{
			LOG.e("[Method:unregisterObserver] unregister observer fail, uri is empty.");
			return;
		}

		if (observerMap == null)
		{
			LOG.e("[Method:unregisterObserver] unregister observer fail, container map is null.");
			return;
		}

		if (observerMap.containsKey(uri) && observerMap.get(uri) != null)
		{
			observerMap.get(uri).unregisterObserver(observer);
		}
	}

	/**
	 *
	 * 该方法的作用: 通知uri对应的所有观察者，数据发生改变
	 *
	 * @date 2014年3月31日
	 * @param entityType
	 */
	public void notifyChange(Class<?> entityType)
	{
		if (entityType == null)
		{
			return;
		}

		String uri = entityType.getName();
		if (observerMap == null || observerMap.get(uri) == null)
		{
			return;
		}

		((TGDatasetObservable) observerMap.get(uri)).notifyChange();
	}
}
