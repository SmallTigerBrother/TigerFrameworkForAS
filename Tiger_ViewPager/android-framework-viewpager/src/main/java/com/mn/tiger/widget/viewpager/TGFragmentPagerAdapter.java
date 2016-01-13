package com.mn.tiger.widget.viewpager;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义的FragmentPagerAdapter
 */
public class TGFragmentPagerAdapter extends FragmentPagerAdapter
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 所有页面
	 */
	private ArrayList<Fragment> pagers;

	public TGFragmentPagerAdapter(FragmentManager fm, List<Fragment> pagers)
	{
		super(fm);
		this.pagers = new ArrayList<Fragment>();
		if(null != pagers)
		{
			this.pagers.addAll(pagers);
		}
	}

	@Override
	public Fragment getItem(int page)
	{
		return pagers.get(page);
	}

	@Override
	public int getCount()
	{
		return pagers.size();
	}

	/**
	 * 获取所有界面
	 * @return
	 */
	public ArrayList<Fragment> getPagers()
	{
		return pagers;
	}
}
