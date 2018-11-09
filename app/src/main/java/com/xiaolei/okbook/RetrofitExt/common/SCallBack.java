package com.xiaolei.okbook.RetrofitExt.common;

import android.app.Fragment;
import android.content.Context;

/**
 * SimpleICallBack
 * Created by xiaolei on 2017/4/6.
 */

public abstract class SCallBack<T> extends SICallBack<T>
{
    public SCallBack(Context context)
    {
        super(context);
    }

    public SCallBack(Fragment fragment)
    {
        super(fragment);
    }

    public SCallBack(android.support.v4.app.Fragment fragment)
    {
        super(fragment);
    }

    @Override
    public abstract void onSuccess(T result) throws Exception;

    @Override
    public void onCache(T result) throws Exception
    {
        onSuccess(result);
    }

    @Override
    public void onFail(Throwable t)
    {
        super.onFail(t);
    }

    @Override
    public void onFinally() { }
}
