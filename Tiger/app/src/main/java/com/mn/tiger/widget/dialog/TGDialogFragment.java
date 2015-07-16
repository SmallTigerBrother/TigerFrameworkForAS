package com.mn.tiger.widget.dialog;

import java.lang.reflect.Field;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.mn.tiger.log.Logger;

/**
 * 自定义的DialogFragment,修复show时状态异常的bug（在执行activity的onSaveInstances之后不能再调用show方法）
 * @author peng
 *
 */
public class TGDialogFragment extends DialogFragment
{
	private static final Logger LOG = Logger.getLogger(TGDialogFragment.class);
	@Override
	public void show(FragmentManager manager, String tag)
	{
		try
		{
			@SuppressWarnings("rawtypes")
			Class dialogFragmentClass = getDialogFragmentClass();
			Field mDismissed = dialogFragmentClass.getDeclaredField("mDismissed");
			mDismissed.setAccessible(true);
			mDismissed.setBoolean(this, false);
			
			Field mShownByMe = dialogFragmentClass.getDeclaredField("mShownByMe");
			mShownByMe.setAccessible(true);
			mShownByMe.setBoolean(this, true);
		}
		catch (Exception e)
		{
			LOG.e(e);
		}		
		
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
	}
	
	/**
	 * 获取DialogFragmentClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Class getDialogFragmentClass()
	{
		Class superClass = this.getClass().getSuperclass();
		while (!(DialogFragment.class.getName().equals(superClass.getName())))
		{
			superClass = superClass.getSuperclass();
		}
		
		return superClass;
	}
}
