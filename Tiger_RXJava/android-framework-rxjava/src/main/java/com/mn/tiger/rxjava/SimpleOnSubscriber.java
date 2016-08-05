package com.mn.tiger.rxjava;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Tiger on 16/8/5.
 */
public class SimpleOnSubscriber<T> implements Observable.OnSubscribe<T>
{
    private T arg;

    public SimpleOnSubscriber(T arg)
    {
        this.arg = arg;
    }

    @Override
    public void call(Subscriber<? super T> subscriber)
    {
        subscriber.onNext(arg);
        subscriber.onCompleted();
    }
}
