package com.mn.tiger.rxjava;

import android.content.Intent;
import android.util.Log;

import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.core.ActivityObserver;

import rx.Subscriber;

/**
 * Created by Dalang on 2016/6/15.
 */
public abstract class LoadingBarSubscriber<T> extends Subscriber<T> implements ActivityObserver
{
    public TGActionBarActivity activity;

    public LoadingBarSubscriber(TGActionBarActivity activity)
    {
        this.activity = activity;
        activity.registerActivityObserver(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        activity.showLoadingDialog();
    }

    @Override
    public void onCompleted()
    {
        activity.dismissLoadingDialog();
    }

    @Override
    public void onError(Throwable e)
    {
        activity.dismissLoadingDialog();
        if(null != e)
        {
            Log.e("LoadingBarSubscriber","[Method:onError]");
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onStop()
    {

    }

    @Override
    public void onDestroy()
    {
        if(!this.isUnsubscribed())
        {
            this.unsubscribe();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }
}
