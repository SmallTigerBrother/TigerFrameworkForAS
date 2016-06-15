package com.mn.tiger.rxjava;

import rx.Subscriber;

/**
 * Created by Dalang on 2016/6/15.
 * Block onCompleted, onError method
 */
public abstract class SimpleSubscriber<T> extends Subscriber<T>
{
    @Override
    public void onCompleted()
    {
        //do nothing
    }

    @Override
    public void onError(Throwable e)
    {
        //do nothing
    }

    @Override
    public void onNext(T t)
    {

    }
}
