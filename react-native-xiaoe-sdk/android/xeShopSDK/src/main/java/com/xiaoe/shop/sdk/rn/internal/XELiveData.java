package com.xiaoe.shop.sdk.rn.internal;

import android.os.Handler;
import android.os.Looper;

public class XELiveData<T> {
    private T mData;
    private boolean mNotSet = true;

    private Observer<T> mObserver;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public void setValue(T value) {
        mData = value;
        mNotSet = false;
        dispatchingValue();
    }

    public void postValue(final T value) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                setValue(value);
            }
        });
    }

    public void observe(Observer<T> observer) {
        mObserver = observer;
        dispatchingValue();
    }

    public void removeObserver() {
        mObserver = null;
    }

    private void dispatchingValue() {
        if (mNotSet) {
            return;
        }
        if (mObserver != null) {
            mObserver.onChanged(mData);
        }
    }

    public interface Observer<T> {

        void onChanged(T value);
    }
}
