package com.mn.tiger.upload.observe;

import android.database.Observable;

import com.mn.tiger.upload.TGUploadManager;
import com.mn.tiger.upload.TGUploader;

/**
 * 
 * 该类作用及功能说明：用一个key的观察者，（取消）注册观察者到arraylist
 * 
 * @date 2014年3月31日
 */
public class TGUploadObservable extends Observable<TGUploadObserver>
{
	/**
	 * 取消注册
	 */
	@Override
	public void unregisterObserver(TGUploadObserver observer)
	{
		super.unregisterObserver(observer);
		if (mObservers == null || (mObservers != null && mObservers.size() == 0))
		{
			mOnObserverChangeListener.onObserverClear(observer.getKey());
		}
	}

	/**
	 * 取消注册传入key对应的所有观察者
	 */
	@Override
	public void unregisterAll()
	{
		String key = mObservers.get(0).getKey();
		super.unregisterAll();
		mOnObserverChangeListener.onObserverClear(key);
	}

	/**
	 * 
	 * 该方法的作用: 通知观察者
	 * 
	 * @date 2014年3月31日
	 */
	public void notifyChange(Object result)
	{
		TGUploader uploader = (TGUploader)result;
		switch(uploader.getUploadStatus())
		{
			case TGUploadManager.UPLOAD_FAILED:
				for (TGUploadObserver observer : mObservers)
				{
					if(observer != null)
					{
						observer.onFailed(uploader, uploader.getErrorCode(), uploader.getErrorMsg());
					}
				}
				unregisterAll();
				break;
			case TGUploadManager.UPLOAD_SUCCEED:
				for (TGUploadObserver observer : mObservers)
				{
					if(observer != null)
					{
						observer.onSuccess(uploader);
					}
				}
				unregisterAll();
				break;
			case TGUploadManager.UPLOAD_UPLOADING:
				for (TGUploadObserver observer : mObservers)
				{
					if(observer != null && uploader.getFileSize() > 0)
					{
    					int progress = (int) (uploader.getCompleteSize() * 100 / uploader.getFileSize());
    					observer.onProgress(uploader, progress);
					}
				}
				break;
			case TGUploadManager.UPLOAD_PAUSE:
				for (TGUploadObserver observer : mObservers)
				{
					if(observer != null)
					{
						observer.onPause(uploader);
					}
				}
				unregisterAll();
				break;
			case TGUploadManager.UPLOAD_STARTING:
				for (TGUploadObserver observer : mObservers)
				{
					if(observer != null)
					{
						observer.onStart(uploader);
					}
				}
				break;
			default:
				break;
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
	public boolean isExistObserver(TGUploadObserver observer)
	{
		for (TGUploadObserver mObserver : mObservers)
		{
			if (mObserver.getKey().equals(observer.getKey()))
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
		 * 该方法的作用: 清空观察者集合回调方法.
		 * 
		 * @date 2014年3月31日
		 * @param uri
		 *            观察者对应key
		 */
		void onObserverClear(String key);
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
