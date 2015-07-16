package com.mn.tiger.app;

import android.app.Activity;

public class AbstractPresenter
{
	private Activity activity;
	
	public AbstractPresenter(Activity activity)
	{
		this.activity = activity;
	}
	
	public Activity getActivity()
	{
		return activity;
	}
	
	public void sendEvent(Object event)
	{
		TGApplication.getBus().post(event);
	}
	
	public void register2Bus()
	{
		TGApplication.getBus().register(this);
	}
	
	public void unregisterFromBus()
	{
		TGApplication.getBus().unregister(this);
	}
}
